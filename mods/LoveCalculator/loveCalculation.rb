require 'vertx'
require 'LoveCouple'

logger = Vertx.logger
logger.info 'Calculation unit up!'

Vertx::EventBus.register_handler('loveCalculation') do |message|
	loveCouple = message.body
	logger.info 'Received new instruction. New couple: ' + loveCouple.to_s

	couple = LoveCouple.new(loveCouple["name1"], loveCouple["name2"])

	reply = {
		'percentage' => couple.percentage,
		'loveMessage' => couple.getMessage
	}
	message.reply(reply)
end

