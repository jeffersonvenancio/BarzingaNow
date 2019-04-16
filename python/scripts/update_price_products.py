import csv, json
import requests

##ESTE Script atualiza a quantidade de produtos com o valor que estiver no csv e o preco
X = []
Y = []
with open('update_price_products.csv', 'rb') as data:
    reader = csv.reader(data, delimiter=',')
    headers = {'content-type' : 'application/x-www-form-urlencoded', 'Authorization' : 'Bearer NHogQU8SvqDdiFWiJCeQIkDzo1JSEhRH'}
    params = {'sessionKey': '9ebbd0b25760557393a43064a92bae539d962103', 'format': 'xml', 'platformId': 1}
    for line in reader:
        print line
        url = "https://barzinganow.appspot.com/api/product/"
        # url = "http://localhost:8080/api/product/"
        url += line[0]
        url += "/repryce"
        data = {'price': str(line[3]), 'quantity': str(line[2])}
        print str(line[1]), data
        print requests.put(url, data=data, headers=headers, params=params)
