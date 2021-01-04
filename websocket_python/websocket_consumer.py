# -*- coding: utf-8 -*-
"""
Created on Tue Dec  1 21:36:38 2020

@author: Alex
"""

import json
from kafka import KafkaConsumer

consumer = KafkaConsumer("finnhub", bootstrap_servers='localhost:9092', group_id="finnhub")
for message in consumer:
        item = json.loads(message.value.decode())
        price = item['p']
        print(price)
