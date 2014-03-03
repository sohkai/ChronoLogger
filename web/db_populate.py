#!flask/bin/python
from app import app, models, db
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import os, json
from app.models import User
from app.models import Visits
from app.models import Beacon
import random

beacon1 = Beacon(beacon_identifier="Fake1", location="Google X Labs", picture='/static/images/google.jpg')
beacon2 = Beacon(beacon_identifier="Fake2", location="Google Cafeteria", picture='/static/images/googlecafe.jpg')
beacon3 = Beacon(beacon_identifier="Fake3", location="Aperture Laboratories", picture='/static/images/aperture.jpg')

real_beacon1 = Beacon(beacon_identifier="b9407f30-f5f8-466e-aff9-25556b57fe6d991", location="Geekdom SF", picture='/static/images/geekdom.jpg')
real_beacon2 = Beacon(beacon_identifier="b9407f30-f5f8-466e-aff9-25556b57fe6d992", location="Geekdom SF Presentation Room", picture='/static/images/hall.jpg')

db.session.add(real_beacon1)
db.session.add(real_beacon2)

db.session.add(beacon1)
db.session.add(beacon2)
db.session.add(beacon3)

admin_user = User(mail='admin@admin.com', name='Admin', role=1)
db.session.add(admin_user)

user1 = User(mail='sergey@google.com', name='Sergey Brin', picture='/static/images/sergey.jpg', role=0)
db.session.add(user1)

user2 = User(mail='larry@google.com', name='Larry Page', picture='/static/images/larry.jpg', role=0)
db.session.add(user2)

user3 = User(mail='ray@google.com', name='Ray Kurzweil', picture='/static/images/ray.jpg', role=0)
db.session.add(user3)

brett = User(mail='brett@linkedin.com', name='Brett Sun', picture='/static/images/brett.jpg', role=0)
db.session.add(brett)

tim = User(mail='tim@linkedin.com', name='Tim Pei', picture='/static/images/tim.jpg', role=0)
db.session.add(tim)

vlad = User(mail='vlad@linkedin.com', name='Vlad Lyubinets', picture='/static/images/vlad.jpg', role=0)
db.session.add(vlad)

db.session.commit()

# Google visits

visit1 = Visits(user_id=user1.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 9, 30, 0), time_left=None)
visit2 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 11, 45, 0), time_left=None)
visit3 = Visits(user_id=user3.id, beacon_id=beacon3.id, time_entered=datetime.datetime(2014, 03, 2, 12, 45, 0), time_left=None)

visit4 = Visits(user_id=user1.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 1, 11, 30, 0), time_left=datetime.datetime(2014, 03, 1, 15, 30, 0))
visit5 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 16, 10, 0), time_left=datetime.datetime(2014, 03, 1, 17, 0, 0))

db.session.add(visit1)
db.session.add(visit2)
db.session.add(visit3)
db.session.add(visit4)
db.session.add(visit5)

for i in xrange(20):
	visit = Visits(user_id=user1.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 02, i + 2, random.randint(9, 11), random.randint(10, 50), random.randint(1, 59)), time_left=datetime.datetime(2014, 02, i+2, random.randint(16, 18), random.randint(1, 55), random.randint(1, 59)))
	db.session.add(visit)
	db.session.commit()

	if random.randint(1, 5) <= 2:
		visit = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 02, i + 2, random.randint(19, 20), random.randint(10, 20), random.randint(1, 59)), time_left=datetime.datetime(2014, 02, i+2, 20, random.randint(30, 55), random.randint(1, 59)))
		db.session.add(visit)
		db.session.commit()

	if random.randint(1, 2) == 1:
		visit = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 02, i + 2, 8, random.randint(5, 20), random.randint(1, 59)), time_left=datetime.datetime(2014, 02, i+2, 8, random.randint(40, 55), random.randint(1, 59)))
		db.session.add(visit)
		db.session.commit()

	visit = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 02, i + 2, random.randint(9, 10), random.randint(10, 20), random.randint(1, 59)), time_left=datetime.datetime(2014, 02, i+2, random.randint(16, 17), random.randint(10, 55), random.randint(1, 59)))
	db.session.add(visit)
	db.session.commit()

	if random.randint(1, 4) != 4:
		visit = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 02, i + 2, 18, random.randint(5, 20), random.randint(1, 59)), time_left=datetime.datetime(2014, 02, i+2, 18, random.randint(40, 55), random.randint(1, 59)))
		db.session.add(visit)
		db.session.commit()

	if random.randint(1, 2) == 1:
		visit = Visits(user_id=user3.id, beacon_id=beacon3.id, time_entered=datetime.datetime(2014, 02, i + 2, random.randint(11, 14), random.randint(5, 50), random.randint(1, 59)), time_left=datetime.datetime(2014, 02, i+2, random.randint(18, 19), random.randint(40, 55), random.randint(1, 59)))
		db.session.add(visit)
		db.session.commit()

# RackSpace visits

visit1 = Visits(user_id=tim.id, beacon_id=real_beacon1.id, time_entered=datetime.datetime(2014, 03, 1, 12, 35, 0), time_left=datetime.datetime(2014, 03, 1, 21, 30, 0))
visit2 = Visits(user_id=brett.id, beacon_id=real_beacon1.id, time_entered=datetime.datetime(2014, 03, 1, 12, 35, 0), time_left=datetime.datetime(2014, 03, 1, 21, 30, 0))
visit3 = Visits(user_id=vlad.id, beacon_id=real_beacon1.id, time_entered=datetime.datetime(2014, 03, 1, 12, 35, 0), time_left=datetime.datetime(2014, 03, 1, 21, 30, 0))

visit4 = Visits(user_id=tim.id, beacon_id=real_beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 35, 0), time_left=None)
visit5 = Visits(user_id=brett.id, beacon_id=real_beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 35, 0), time_left=None)
visit6 = Visits(user_id=vlad.id, beacon_id=real_beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 35, 0), time_left=None)


#
#visit4 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 9, 30, 0), time_left=datetime.datetime(2014, 03, 2, 11, 30, 0))
#visit5 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 11, 45, 0), time_left=datetime.datetime(2014, 03, 2, 12, 45, 0))
#visit6 = Visits(user_id=user2.id, beacon_id=beacon1.id, time_entered=datetime.datetime(2014, 03, 2, 13, 45, 0), time_left=datetime.datetime(2014, 03, 2, 19, 30, 0))
#visit11 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 9, 30, 0), time_left=datetime.datetime(2014, 03, 1, 11, 30, 0))
#visit12 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 45, 0), time_left=datetime.datetime(2014, 03, 1, 12, 45, 0))
#visit13 = Visits(user_id=user1.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 13, 45, 0), time_left=datetime.datetime(2014, 03, 1, 19, 30, 0))
#visit14 = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 9, 30, 0), time_left=datetime.datetime(2014, 03, 1, 11, 30, 0))
#visit15 = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 11, 45, 0), time_left=datetime.datetime(2014, 03, 1, 12, 45, 0))
#visit16 = Visits(user_id=user2.id, beacon_id=beacon2.id, time_entered=datetime.datetime(2014, 03, 1, 13, 45, 0), time_left=datetime.datetime(2014, 03, 1, 19, 30, 0))
#
db.session.add(visit1)
db.session.add(visit3)
db.session.add(visit2)

db.session.add(visit4)
db.session.add(visit5)
db.session.add(visit6)
#db.session.add(visit11)
#db.session.add(visit12)
#db.session.add(visit13)
#db.session.add(visit14)
#db.session.add(visit15)
#db.session.add(visit16)

db.session.commit()