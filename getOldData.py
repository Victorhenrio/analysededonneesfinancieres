# -*- coding: utf-8 -*-
"""
Created on Mon Mar  1 11:54:51 2021

@author: Victor HENRIO
"""

import requests
import pandas as pd
from datetime import datetime
from pymongo import MongoClient
import json


def request_finnhub(symbol,date_temp,date_end,resolution,token):
    """
    Request data of specific cryptocurrency from finnhub API.
    Processing data to be conform to the schema.
    
    Args:
        symbol: Broker and currency pair.
        date_temp: begining of window in UNIX timestamp.
        date_end: ending of window in UNIX timestamp.
        resolution: timeframe (1, 5, 15, 30, 60, D, W, M).
        token: Private Token.
    
    Returns:
        Returned a dataframe with the data during the specific period . 
    """

    uri = "https://finnhub.io/api/v1/crypto/candle?symbol="+ symbol+"&resolution="+ resolution + "&from="+ str(date_temp) +"&to="+ str(date_end) +"&token="+ token
    r = requests.get(uri)
    df = pd.read_json(r.text)
    
    df_clean = df.drop(['h','l','o','s'],axis=1)
    df_clean['symbol']='BINANCE:BTCUSDT'
    df_clean = df_clean.rename(columns={"c": "price", "t": "unix_time", "v": "volume"})
    df_clean = df_clean[['symbol', 'price','volume','unix_time']]
    df_clean['date'] = pd.to_datetime(df_clean['unix_time'], unit='s')  
    df_clean.date.dt.tz_localize('UTC').dt.tz_convert('Europe/Paris')
    
    return(df_clean)


def request_loop(symbol,date_start,date_end,resolution,token): 
    """
    Call the function request_finnhub x times to get all the data from the period.
    This is due to a limitation of 500 rows from finnhub API.
    
    Args:
        symbol: Broker and currency pair.
        date_start: begining of window in UNIX timestamp.
        date_end: ending of window in UNIX timestamp.
        resolution: timeframe (1, 5, 15, 30, 60, D, W, M).
        token: Private Token.
    
    Returns:
        Returned a dataframe taht concat all the data during the specific period . 
    """
    df_tab = []
    date_temp = date_start
    while date_temp < date_end : 
        data = request_finnhub(symbol, date_temp, date_end, resolution, token)
        df_tab.append(data)
        date_temp += 30000 # The equivalent of 500 minutes, to be adjusted according to the resolution
        print("#"*45)
        print("date_start :",date_temp)
        print("date_end :",date_end)
        print(data)
        print("#"*45,"\n")
        
    df = pd.concat(df_tab)
    df['unix_time'] = df['unix_time'].map(lambda timestamp: timestamp*1000)
    print("Print premier")
    print(df)
    return df


def save_on_mongo(data):
    """
    Connect to mongodb and save the dataframe in the database.
    
    Args:
        data : dataframe containing symbol,price,volume,unix_time and date. 
    
    Returns:
        None . 
    """
    
    client = MongoClient("mongodb://root:toto@34.94.185.118:27017/test.collection?authSource=admin")
    db = client['test']
    collection = db['collection']
    data_dict = data.to_dict('r')
    collection.insert_many(data_dict)
    



def main():
    data = request_loop("BINANCE:BTCUSDT", 1614540200, 1614639600, "1", "but5p6n48v6uea8ajc4g")
    print(data)
    print("############")
    print(data.shape)
    save_on_mongo(data)
    return data




if __name__ == "__main__":
    data = main()
    
    