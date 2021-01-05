# -*- coding: utf-8 -*-
"""
Created on Thu Dec 31 07:39:38 2020

@author: Victor HENRIO
"""

from google.cloud import pubsub_v1
import websocket


def on_message(ws, message):
    print(message)
    publish_data_to_GCP(message)

def on_error(ws, error):
    print(error)

def on_close(ws):
    print("### closed ###")

def on_open(ws):
    ws.send('{"type":"subscribe","symbol":"AAPL"}')
    ws.send('{"type":"subscribe","symbol":"AMZN"}')
    ws.send('{"type":"subscribe","symbol":"BINANCE:BTCUSDT"}')
    ws.send('{"type":"subscribe","symbol":"IC MARKETS:1"}')


def publish_data_to_GCP(data):
    #TODO(developer)
    print("#"*30)
    project_id = "utility-destiny-300118"
    topic_id = "my-topic"
    
    publisher = pubsub_v1.PublisherClient()
    # The `topic_path` method creates a fully qualified identifier
    # in the form `projects/{project_id}/topics/{topic_id}`
    topic_path = publisher.topic_path(project_id, topic_id)
      
    future = publisher.publish(topic_path, data.encode('utf-8'))
    print(future.result())
    
    print(f"Published some messages to {topic_path}.")

if __name__ == "__main__":
    websocket.enableTrace(True)
    ws = websocket.WebSocketApp("wss://ws.finnhub.io?token=but5p6n48v6uea8ajc4g",
                              on_message = on_message,
                              on_error = on_error,
                              on_close = on_close)
    ws.on_open = on_open
    
    ws.run_forever()
