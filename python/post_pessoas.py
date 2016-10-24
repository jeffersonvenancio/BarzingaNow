import csv, json
import requests

X = []
Y = []
with open('pessoas.csv', 'rb') as data:
	reader = csv.reader(data, delimiter=',')
	headers = {'content-type' : 'application/x-www-form-urlencoded'}
	params = {'sessionKey': '9ebbd0b25760557393a43064a92bae539d962103', 'format': 'xml', 'platformId': 1}
	for line in reader:
		#url = "http://barzinganow.appspot.com/api/user"
		url = "http://localhost:8080/api/user/"
		data = {"name":line[0],"email":line[3]}
		print requests.post(url, data=data, headers=headers, params=params)


