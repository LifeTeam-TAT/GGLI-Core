package org.ace.insurance.system.common.channel.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.channel.persistence.interfaces.IChannelDAO;
import org.ace.insurance.system.common.channel.service.interfaces.IChannelService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ChannelService")
public class ChannelService implements IChannelService {

	@Resource(name = "ChannelDAO")
	private IChannelDAO channelDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Channel channel) {
		try {
			channelDAO.insert(channel);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a channel", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Channel channel) {
		try {
			channelDAO.delete(channel);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a channel", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Channel channel) {
		try {
			channelDAO.update(channel);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a channel", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Channel> findAllChannel() {
		List<Channel> result = null;
		try {
			result = channelDAO.findAllChannel();

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a channel", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Channel findByChannelId(String channelId) {
		Channel result = null;
		try {
			result = channelDAO.findById(channelId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find school", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingChannel(Channel channel) {

		return channelDAO.checkExistingChannel(channel);
	}

}
