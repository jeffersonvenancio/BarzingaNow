import csv, json
import requests

X = []
Y = []
with open('products.csv', 'rb') as data:
    reader = csv.reader(data, delimiter=',')
    headers = {'content-type' : 'application/x-www-form-urlencoded', 'X-Appengine-Cron' : 'true'}
    params = {'sessionKey': '9ebbd0b25760557393a43064a92bae539d962103', 'format': 'xml', 'platformId': 1}
    for line in reader:
        url = "https://cron-dot-barzinganow.appspot.com/api/product/"
        # url = "http://localhost:8080/api/product/"
        url += line[0]
        url += "/repryce"
        data = {"description":line[1],"price":line[2], "quantity": int(line[4])}
        print data
        print requests.put(url, data=data, headers=headers, params=params)