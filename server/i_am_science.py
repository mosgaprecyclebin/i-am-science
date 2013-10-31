import tornado.ioloop
import tornado.web
import tornado.httpserver
import tornado.httputil
import json
import time
from pymongo import *
import random
import threading
from tornado.options import define, options


# USAGE:
# curl -v http://streaming.coenosense.com:5556/addstat -X POST --data-binary '{"data": {"bla": "blabla"}}' -H "Content-type: application/json"

#  Environment
define("env", default="itet", help="Environment in which the receiver is executed", type=str)
define("port", default=8092, help="Port on which the server listens", type=int)

def set_proc_name(newname):
    from ctypes import cdll, byref, create_string_buffer
    libc = cdll.LoadLibrary('libc.so.6')
    buff = create_string_buffer(len(newname)+1)
    buff.value = newname
    libc.prctl(15, byref(buff), 0, 0, 0)

# collects app statistics
class StatisticsHandler(tornado.web.RequestHandler):
    def get(self):
	
        uid = self.get_argument('uid')
	appid = self.get_argument('appid') # 0 und 100
        score = int(self.get_argument('score'))
 #      appspecific_data = tornado.escape.json_decode(self.request.body) # score, more_data
        t_upload = time.time()
        # update stats
        userstatsCollection.update({'uid':uid, 'appid':appid},{'uid':uid, 'appid':appid, 'score':score, 'lastupdate':t_upload},upsert=True);
        result = {'success':True};
	if (usersCollection.find({'uid':uid}).count()==0):
		usersCollection.insert({'uid':uid, 'registration_time':t_upload});
	logConnection.insert({'uid':uid,'type':'update','time':t_upload})
        self.write(result)

class GetIamScienceDataHandler(tornado.web.RequestHandler):
    def get(self):
        uid = self.get_argument('uid')
        stats = list(userstatsCollection.find({'uid':uid}))
	statsByAppId = {}
	for s in stats:
		statsByAppId[s['appid']]=s 
	# query user data: apps and app stats: maybe there is a more elegant way of shortening the code
	apps = list(appdataCollection.find())
	res = {};	
	res['apps'] = []
	for a in apps: 
		if a['id'] in statsByAppId.keys():
			us = statsByAppId[a['id']]
			print "HIIIT"
			res['apps'].append({'id': a['id'],'logo':a['logo'],'installed':True,'score':int(us['score']),'lastupdate:':us['lastupdate']})
		else:
			res['apps'].append({'id': a['id'],'logo':a['logo'],'installed':False, 'score':0})

	print res
        t_request = time.time()
	logConnection.insert({'uid':uid,'type':'get_i_am_science_data','time':t_request})
	result = res
        self.write(result)

# populates the database with initial app data: hardcoded example
class PopulateHandler(tornado.web.RequestHandler):
    def get(self):
	apps = [{'id':'ch.ethz.nervous','logo':'http://coenosense.com/iamscience/ch.ethz.nervous.png'},{'id':'ch.ethz.showmeyourworld','logo':'http://coenosense.com/iamscience/ch.ethz.showmeyourworld.png'},{'id':'ch.ethz.planet','logo':'http://coenosense.com/iamscience/ch.ethz.planet.png'}]
	for a in apps:
		appdataCollection.update({'id':a['id']}, a, upsert=True);	

class RankingHandler(tornado.web.RequestHandler):
    def get(self):
	users = usersCollection.find()
	for u in users:
		stats = userStatsCollection.find({'uid':u['uid']})
		
	# per app statistics
	# overall statistics
	self.write('not implemented yet')

application = tornado.web.Application([
    (r"/populate", PopulateHandler),
    (r"/update", StatisticsHandler),
    (r"/get_i_am_science_data",GetIamScienceDataHandler),
    (r"/ranking",RankingHandler)
])

# TODO: push notificaiton

if __name__ == "__main__":
    set_proc_name("pyiamscience")
    tornado.options.parse_command_line()
    
    #  Setup database connections based on environment
    try:
        if options.env == "aws":
            mongoConnection = Connection('mongo-shard-1.coenosense.com', 55001) # mongos
        elif options.env == "itet":
            mongoConnection = Connection('drmabuse.ee.ethz.ch', 55001) # mongos
        else:
            print("ERROR: Invalid environment specified")
            logger.error('Invalid environment specified, server will now exit')
            exit()
    except errors.ConnectionFailure: # from pymongo
        print("ERROR: Can't connect to the database!")
        logger.error('Can not connect to database, server will now exit')
        exit()
    usersCollection = mongoConnection['iamscience']['users']
    userstatsCollection = mongoConnection['iamscience']['userstats']
    appdataCollection = mongoConnection['iamscience']['appdata']
    logConnection = mongoConnection['iamscience']['log'] 
    application.listen(options.port)#, body_handlers=body_handlers)
    mainloop = tornado.ioloop.IOLoop.instance()

    print("I am Science Server started")
    mainloop.start()

    
