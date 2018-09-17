from google.appengine.ext import ndb
import math

class User(ndb.Model):
    name = ndb.StringProperty()
    money = ndb.FloatProperty()
    email = ndb.StringProperty()
    photo_url = ndb.StringProperty()
    admin = ndb.BooleanProperty()
    pin = ndb.StringProperty()
    rfid = ndb.StringProperty()

    def to_dict(self):
        result = super(User, self).to_dict()
        result['id'] = self.key.id()

        return result

    def debit(self, value):
        # money = math.ceil(self.money) + 20
        #
        # if value > money:
        #     raise Exception('Saldo insuficiente')
        self.money -= value

    def credit(self, value):
        self.money += value