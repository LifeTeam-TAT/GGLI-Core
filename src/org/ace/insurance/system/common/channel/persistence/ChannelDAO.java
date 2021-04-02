package org.ace.insurance.system.common.channel.persistence;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.channel.persistence.interfaces.IChannelDAO;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("ChannelDAO")
public class ChannelDAO extends BasicDAO implements IChannelDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Channel channel) throws DAOException {
		try {
			if (!checkExistingChannel(channel)) {
				em.persist(channel);
			} else {
				throw new SystemException(null, channel.getName() + " is already exist.");
			}
		} catch (PersistenceException e) {
			throw translate("Failed to insert channel (channelname = " + channel.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Channel channel) throws DAOException {
		try {
			channel = em.merge(channel);
			em.remove(channel);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to delete channel User(channel name = " + channel.getName() + ")", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Channel channel) {
		try {
			em.merge(channel);
			em.flush();
		} catch (PersistenceException e) {
			throw translate("Failed to update channel User(channelname = " + channel.getName() + ")", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Channel> findAllChannel() throws DAOException {
		List<Channel> result = null;
		try {
			Query q = em.createQuery("select c from Channel c ");
			result = q.getResultList();
		} catch (PersistenceException e) {
			throw translate("Failed to  find all channel ", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean checkExistingChannel(Channel channel) throws DAOException {
		boolean exist = false;
		String channelName = channel.getName().replaceAll("\\s+", "");
		try {
			StringBuffer buffer = new StringBuffer();
			Query query = null;

			buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.name) > 0) THEN TRUE ELSE FALSE END FROM Channel c ");
			buffer.append(" WHERE LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
			buffer.append(channel.getId() != null ? "AND c.id != :id" : "");
			query = em.createQuery(buffer.toString());
			if (channel.getId() != null)
				query.setParameter("id", channel.getId());
			query.setParameter("name", channelName.toLowerCase());
			exist = (Boolean) query.getSingleResult();

			if (!exist) {
				buffer = new StringBuffer("SELECT CASE WHEN (COUNT(c.id) > 0) THEN TRUE ELSE FALSE END FROM Channel c");
				buffer.append(" WHERE c.id != :id");
				buffer.append(" AND LOWER(FUNCTION('REPLACE',c.name,' ','')) = :name ");
				query = em.createQuery(buffer.toString());
				query.setParameter("id", channel.getId());
				query.setParameter("name", channel.getName());
				exist = (Boolean) query.getSingleResult();
			}
			em.flush();
			return exist;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Existing Name ", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Channel findById(String ChannelId) throws DAOException {
		Channel result = null;
		try {
			result = em.find(Channel.class, ChannelId);
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find channel", pe);
		}
		return result;
	}

}
