from google.appengine.ext import ndb
from functools import reduce

from user.model import User
from product.model import Product

class TransactionItem(ndb.Model):
    product = ndb.KeyProperty(kind=Product)
    quantity = ndb.IntegerProperty()

class Transaction(ndb.Model):
    user = ndb.KeyProperty(kind=User)
    items = ndb.KeyProperty(kind=TransactionItem, repeated=True)
    value = ndb.FloatProperty()
    date = ndb.DateTimeProperty(auto_now_add=True)


    @staticmethod
    @ndb.transactional(xg=True)
    def new(user, products, quantity_table):
        transactionItems = []
        value = 0
        try:
            for product in products:
                quantity = quantity_table[product.key.id()]

                product.buy(quantity)
                product.put()

                transactionItem = TransactionItem(product=product.key, quantity=quantity)
                transactionItem.put()

                transactionItems.append(transactionItem.key)

                value += product.price * quantity

            user.debit(value)
            user.put()
        except Exception:
            ndb.Rollback()
            raise

        return Transaction(user=user.key, items=transactionItems, value=value)