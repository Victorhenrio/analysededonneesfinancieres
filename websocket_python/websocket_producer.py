# -*- coding: utf-8 -*-
"""
Created on Mon Nov 30 16:16:52 2020

@author: Alex
"""

import json                                                                                                
import time
import urllib.request
import websocket
from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers="localhost:9092")
   
def on_message(ws, message):
    producer.send("finnhub", json.dumps(message).encode())
    print(message)

def on_error(ws, error):
    print(error)

def on_close(ws):
    print("### closed ###")

def on_open(ws):
    ws.send('{"type":"subscribe","symbol":"AAPL"}')
    ws.send('{"type":"subscribe","symbol":"AMZN"}')
    ws.send('{"type":"subscribe","symbol":"BINANCE:BTCUSDT"}')
    ws.send('{"type":"subscribe","symbol":"IC MARKETS:1"}')

while True:
    if __name__ == "__main__":
        websocket.enableTrace(True)
        ws = websocket.WebSocketApp("wss://ws.finnhub.io?token=buud3kv48v6r2rd2kbf0",
                                  on_message = on_message,
                                  on_error = on_error,
                                  on_close = on_close)
        ws.on_open = on_open
        ws.run_forever()