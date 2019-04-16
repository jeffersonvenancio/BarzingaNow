import csv, json
import requests

##ESTE Script atualiza a quantidade de produtos somando o valor que estiver no csv
X = []
Y = []
with open('corrige_qtd_products.csv', 'rb') as data:
    reader = csv.reader(data, delimiter=',')
    headers = {'content-type' : 'application/x-www-form-urlencoded', 'Authorization' : 'Bearer NHogQU8SvqDdiFWiJCeQIkDzo1JSEhRH'}
    params = {'sessionKey': '9ebbd0b25760557393a43064a92bae539d962103', 'format': 'xml', 'platformId': 1}
    for line in reader:
        url = "https://barzinganow.appspot.com/api/product/"
        url += line[0]
        url += "/07-01-2019"
        url += "/"
        url += str(line[4])
        print str(line[1]), str(line[4]), url
        print requests.post(url, headers=headers, params=params)
