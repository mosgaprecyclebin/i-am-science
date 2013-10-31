
# Starts the dedicated coenofire mongoDB instance, for details see http://www.coenosense.com/wiki/index.php?title=Server_guests

# Launch mongod instance as daemon with coenofire config
export LC_ALL=C
mongod --fork --port 55001 --dbpath /opt/iamscience/db --logpath /opt/iamscience/log/mongodb-iamscience.log --logappend
