require 'vertx'
require 'xmlsimple'

class LoveCouple
	messageData = File.read(ENV['VERTX_MODS'] + File::ALT_SEPARATOR + 'LoveCalculator' + File::ALT_SEPARATOR + 'resources' + File::ALT_SEPARATOR + 'loveMessages.xml')
	@@loveMessages = XmlSimple.xml_in(messageData)
	Vertx.logger.info 'LoveMessages: ' + @@loveMessages.to_s
	attr_accessor :name1, :name2, :percentage

	def initialize(name1, name2)
		@name1 = name1
		@name2 = name2
		calcPercentage
	end

	def calcPercentage
		charSum = 0
		(@name1+@name2).bytes do |byte|
			charSum += byte
		end
		@percentage = charSum % 100
	end

	def getMessage

		messageText = ''

		@@loveMessages["message"].each do |message|
			if message["minvalue"].to_i <= @percentage && message["maxvalue"].to_i >= @percentage
				messageText = messageText + ' ' + message["message"].to_s
			end
		end
		return messageText

	end
end