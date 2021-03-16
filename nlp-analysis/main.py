# -*- coding: utf-8 -*-
"""
Created on Wed Mar 10 17:12:38 2021

@author: Victor HENRIO
"""

import pymongo
import pandas as pd

import requests
from bs4 import BeautifulSoup
import crypto_benchmark as cry_bench




def get_info_crypto():
    """
    Get list and information of top 200 cypto-currency from "coinmarketcap.com" 
    
    Returns:
        DataFrame containing all the data 
    """
   
    name_list = []
    cap_list = []
    pourcentage_7j_list = []
    pourcentage_24h_list = []    
    total_coin_list = []
    crypto_page = requests.get("https://coinmarketcap.com/all/views/all/")
    soup = BeautifulSoup(crypto_page.text, 'lxml')  
    soup = soup.find('tbody')  
    for td in soup.find_all('tr'):  
        print(td)
        name = td.select('tr > td')[1].get_text(strip=True)
        pourcentage_24h = td.select('tr > td')[8].get_text(strip=True)
        pourcentage_7j = td.select('tr > td')[9].get_text(strip=True)
        cap = td.select('tr > td')[3].get_text(strip=True)
        total_coin = td.select('tr > td')[5].get_text(strip=True)
        print(name)
        print(cap)
        print(pourcentage_24h)
        print(pourcentage_7j)
        print("#"*100)
        name_list.append(name)
        pourcentage_24h_list.append(pourcentage_24h)
        pourcentage_7j_list.append(pourcentage_7j)
        cap_list.append(cap)
        total_coin_list.append(total_coin)
        
    d_crypto ={}
    d_crypto = {"name":name_list, "pourcentage_24h":pourcentage_24h_list, "pourcentage_7j": pourcentage_7j_list, "cap":cap_list, "total_coin": total_coin_list}
    df_cryto_data = pd.DataFrame(d_crypto)
    
    return df_cryto_data


def insert_data_to_mongo(df_cryto_data,dataBaseName):
    
    client = pymongo.MongoClient(r"mongodb+srv://admin:toto@cluster0.odyjd.mongodb.net/?retryWrites=true&w=majority")
    db = client['test']
    collection = db[dataBaseName]
    df_cryto_data.reset_index(inplace=True)
    data_dict = df_cryto_data.to_dict("records")
    # Insert collection
    collection.insert_many(data_dict)
    
    


def main():
    
    df_cryto_data = get_info_crypto()
    insert_data_to_mongo(df_cryto_data)
    df_cryto_nlp = cry_bench.benchmark(3)
    insert_data_to_mongo(df_cryto_nlp)
    
    
    


if __name__ == "__main__":
    main()

    


    

    
    
    