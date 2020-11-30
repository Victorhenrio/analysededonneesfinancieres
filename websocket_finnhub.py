# -*- coding: utf-8 -*-
"""
Created on Tue Nov 24 10:28:54 2020

@author: Alex
"""

import requests

# Register new webhook for earnings
r = requests.post('https://finnhub.io/api/v1/webhook/add?token=buud3kv48v6r2rd2kbf0', json={'event': 'earnings', 'symbol': 'AAPL'})
res = r.json()
print(res)

webhook_id = res['id']
# List webhook
r = requests.get('https://finnhub.io/api/v1/webhook/list?token=buud3kv48v6r2rd2kbf0')
res = r.json()
print(res)

#Delete webhook
r = requests.post('https://finnhub.io/api/v1/webhook/delete?token=buud3kv48v6r2rd2kbf0', json={'id': webhook_id})
res = r.json()
print(res)

#%%

import websocket

def on_message(ws, message):
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

if __name__ == "__main__":
    websocket.enableTrace(True)
    ws = websocket.WebSocketApp("wss://ws.finnhub.io?token=buud3kv48v6r2rd2kbf0",
                              on_message = on_message,
                              on_error = on_error,
                              on_close = on_close)
    ws.on_open = on_open
    ws.run_forever()