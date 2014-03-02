#!flask/bin/python
from app import app, models, db
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import os, json
from app.models import User
from app.models import Visits
from app.models import Beacon

beacon1 = Beacon(beacon_identifier="FakeSoFake1", location="Conference Room", picture='/static/images/bathroom.jpg')
beacon2 = Beacon(beacon_identifier="FakeSoFake2", location="Cafe", picture='/static/images/linkedin1.jpg')
db.session.add(beacon1)
db.session.add(beacon2)

admin_user = User(mail='jeff@jeff.com', name='Jeff', role=1)
db.session.add(admin_user)

user1 = User(mail='user1@jeff.com', name='Bob', role=0)
db.session.add(user1)

user2 = User(mail='user2@jeff.com', name='Josh', role=0)
db.session.add(user2)

db.session.commit()

visit1 = Visits(user_id=user1.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 9, 30, 0), time_left=datetime.datetime(2014, 03, 2, 11, 30, 0))
visit2 = Visits(user_id=user1.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 11, 45, 0), time_left=datetime.datetime(2014, 03, 2, 12, 45, 0))
visit3 = Visits(user_id=user1.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 13, 45, 0), time_left=datetime.datetime(2014, 03, 2, 19, 30, 0))
visit4 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 9, 30, 0), time_left=datetime.datetime(2014, 03, 2, 11, 30, 0))
visit5 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 11, 45, 0), time_left=datetime.datetime(2014, 03, 2, 12, 45, 0))
visit6 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 13, 45, 0), time_left=datetime.datetime(2014, 03, 2, 19, 30, 0))
visit11 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 9, 30, 0), time_left=datetime.datetime(2014, 03, 1, 11, 30, 0))
visit12 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 45, 0), time_left=datetime.datetime(2014, 03, 1, 12, 45, 0))
visit13 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 13, 45, 0), time_left=datetime.datetime(2014, 03, 1, 19, 30, 0))
visit14 = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 9, 30, 0), time_left=datetime.datetime(2014, 03, 1, 11, 30, 0))
visit15 = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 45, 0), time_left=datetime.datetime(2014, 03, 1, 12, 45, 0))
visit16 = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 13, 45, 0), time_left=datetime.datetime(2014, 03, 1, 19, 30, 0))

db.session.add(visit1)
db.session.add(visit2)
db.session.add(visit3)
db.session.add(visit4)
db.session.add(visit5)
db.session.add(visit6)
db.session.add(visit11)
db.session.add(visit12)
db.session.add(visit13)
db.session.add(visit14)
db.session.add(visit15)
db.session.add(visit16)

db.session.commit()