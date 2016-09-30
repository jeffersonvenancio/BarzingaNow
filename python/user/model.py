from google.appengine.ext import ndb

class User(ndb.Model):
    name = ndb.StringProperty()
    money = ndb.FloatProperty()

    def debit(self, value):
        self.money -= value

    def credit(self, value):
        self.money += value
