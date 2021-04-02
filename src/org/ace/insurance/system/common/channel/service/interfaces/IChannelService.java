package org.ace.insurance.system.common.channel.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.channel.Channel;

public interface IChannelService {

	public void insert(Channel channel);

	public void delete(Channel channel);

	public void update(Channel channel);

	public Channel findByChannelId(String channelId);

	public List<Channel> findAllChannel();

	public boolean checkExistingChannel(Channel channel);

}
