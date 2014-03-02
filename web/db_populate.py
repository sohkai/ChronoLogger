#!flask/bin/python
from app import app, models, db
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import os, json
from app.models import User
from app.models import Visits
from app.models import Beacon

beacon = Beacon(beacon_identifier="FakeSoFake", location="RackSpace bathroom")
db.session.add(beacon)

admin_user = User(mail='jeff@jeff.com', name='Jeff', role=1)
db.session.add(admin_user)

user1 = User(mail='user1@jeff.com', name='Bob', role=0)
db.session.add(user1)

user2 = User(mail='user2@jeff.com', name='Josh', role=0)
db.session.add(user2)

db.session.commit()

visit1 = Visits(user_id=user1.id, beacon_id=beacon.id, time_entered=datetime.datetime.utcnow(), time_left=datetime.datetime.utcnow())
visit2 = Visits(user_id=user2.id, beacon_id=beacon.id, time_entered=datetime.datetime.utcnow(), time_left=datetime.datetime.utcnow())

db.session.add(visit1)
db.session.add(visit2)

db.session.commit()