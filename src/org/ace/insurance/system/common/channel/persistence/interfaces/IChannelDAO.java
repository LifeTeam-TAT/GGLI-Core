package org.ace.insurance.system.common.channel.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.channel.Channel;
import org.ace.java.component.persistence.exception.DAOException;

public interface IChannelDAO {

	public void insert(Channel channel) throws DAOException;

	public void delete(Channel channel) throws DAOException;

	public void update(Channel channel) throws DAOException;

	public Channel findById(String ChannelId) throws DAOException;

	public List<Channel> findAllChannel();

	public boolean checkExistingChannel(Channel channel) throws DAOException;

}
