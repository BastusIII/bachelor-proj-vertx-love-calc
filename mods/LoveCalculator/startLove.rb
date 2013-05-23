require "vertx"
include Vertx

Vertx.logger.info("Initializing Love Calculator")
Vertx.deploy_verticle('loveCalculation.rb')
Vertx.deploy_verticle('LoveServer.java')

