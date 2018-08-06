import csv, json
import requests

X = []
Y = []
with open('pessoas_rfid.csv', 'rb') as data:
    reader = csv.reader(data, delimiter=',')
    headers = {'content-type' : 'application/x-www-form-urlencoded', 'Bearer' : 'Token Diego'}
    params = {'sessionKey': '9ebbd0b25760557393a43064a92bae539d962103', 'format': 'xml', 'platformId': 1}
    for line in reader:
        # url = "http://barzinganow.appspot.com/api/user/"
        url = "http://localhost:8080/api/user/rfid"
        data = {"rfid":str(line[1]),"email":line[0]}
        print requests.put(url, data=data, headers=headers, params=params)