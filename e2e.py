#!/usr/bin/env python3

import requests
import unittest
import psycopg2
import json
from kafka import KafkaConsumer

connection = psycopg2.connect(user="postgres",
                              password="postgres",
                              host="127.0.0.1",
                              port="5432",
                              database="taskdb")

consumer = KafkaConsumer('phones.reservations', group_id='group', bootstrap_servers=['localhost:9092'], consumer_timeout_ms=10000, auto_offset_reset='earliest')

def get_reserved_phones_by_id(user_id):
    cursor = connection.cursor()
    cursor.execute(f'select phone_id from mobile_phone_reservation where user_id = {user_id}')
    return [row[0] for row in cursor.fetchall()]


class TestReservation(unittest.TestCase):
    def test_return_401_wehn_reserve_unauthorized(self):
        response = requests.post("http://localhost:8080/reserve", json = {'phoneId': 1}, headers={'Content-type': 'application/json'})
        self.assertEqual(response.status_code, 401)

    def test_return_401_when_release_unauthorized(self):
        response = requests.post("http://localhost:8080/release", json = {'phoneId': 1}, headers={'Content-type': 'application/json'})
        self.assertEqual(response.status_code, 401)

    def test_book_release_persistence_and_message(self):
        #book
        response = requests.post("http://localhost:8080/reserve", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 200)
        self.assertEqual(get_reserved_phones_by_id(1), [1])

        message = json.loads(next(consumer).value)
        self.assertEqual(message['phoneId'], 1)
        self.assertEqual(message['userId'], 1)
        self.assertEqual(message['reservationStatus'], 'BOOKED')

        #release
        response = requests.post("http://localhost:8080/release", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 200)
        message = json.loads(next(consumer).value)
        self.assertEqual(message['phoneId'], 1)
        self.assertEqual(message['userId'], 1)
        self.assertEqual(message['reservationStatus'], 'RELEASED')

    def test_return_500_when_book_twice_not_allowed(self):
        response = requests.post("http://localhost:8080/reserve", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 200)
        response = requests.post("http://localhost:8080/reserve", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 500)
        requests.post("http://localhost:8080/release", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})

    def test_return_500_when_release_different_user_not_allowed(self):
        response = requests.post("http://localhost:8080/reserve", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 200)
        response = requests.post("http://localhost:8080/release", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '2'})
        self.assertEqual(response.status_code, 500)
        response = requests.post("http://localhost:8080/release", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 200)

    def test_return_500_when_release_not_booked_not_allowed(self):
        response = requests.post("http://localhost:8080/release", json = {'phoneId': 1}, headers={'Content-type': 'application/json', "X-User-Id": '1'})
        self.assertEqual(response.status_code, 500)

    def test_return_404_for_unknown_booking(self):
        response = requests.get("http://localhost:8080/booking?phoneId=1")
        self.assertEqual(response.status_code, 404)

    def test_return_booking_info_for_existing_booking(self):
        response = requests.post("http://localhost:8080/reserve", json = {'phoneId': 5}, headers={'Content-type': 'application/json', "X-User-Id": '2'})
        self.assertEqual(response.status_code, 200)
        response = requests.get("http://localhost:8080/booking?phoneId=5")
        response_body = json.loads(response.content)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response_body['userId'], 2)
        self.assertEqual(response_body['phoneId'], 5)
        requests.post("http://localhost:8080/release", json = {'phoneId': 5}, headers={'Content-type': 'application/json', "X-User-Id": '2'})

    def test_return_all_phones(self):
        response_body = sorted([elem['modelName'] for elem in json.loads(requests.get("http://localhost:8080/phones").content)])
        self.assertEqual(response_body, [
            'Apple iPhone 11',
            'Apple iPhone 12',
            'Apple iPhone 13',
            'Motorola Nexus 6',
            'Nokia 3310',
            'Oneplus 9',
            'Samsung Galaxy S8',
            'Samsung Galaxy S8',
            'Samsung Galaxy S9',
            'iPhone X'])

if __name__ == '__main__':
    unittest.main()