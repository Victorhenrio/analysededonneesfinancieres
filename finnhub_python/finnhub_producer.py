# -*- coding: utf-8 -*-
"""
Created on Mon Nov 30 16:16:52 2020

@author: Alex
"""

import json                                                                                                
import time
import urllib.request

from kafka import KafkaProducer

API_KEY = "buud3kv48v6r2rd2kbf0" # FIXME Set your own API key here
url = "https://finnhub.io/api/v1/quote?symbol=AAPL&token={}".format(API_KEY)

producer = KafkaProducer(bootstrap_servers="localhost:9092")

while True:
    response = urllib.request.urlopen(url)
    value = json.loads(response.read().decode())

    producer.send("finnhub", json.dumps(value).encode())
    print("{} Produced {} value records".format(time.time(), len(value)))
    time.sleep(1)