# -*- coding: utf-8 -*-
"""
Created on Mon Nov 30 16:17:28 2020

@author: Alex
"""

import json
from kafka import KafkaConsumer

consumer = KafkaConsumer("finnhub", bootstrap_servers='localhost:9092', group_id="finnhub")
for message in consumer:
        station = json.loads(message.value.decode())
        price = station['c']
        print(price)
        #print(station)