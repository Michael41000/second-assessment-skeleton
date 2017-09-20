package com.cooksys.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.dto.ContentCredentialDto;
import com.cooksys.dto.ReplyTweetDto;
import com.cooksys.dto.RepostTweetDto;
import com.cooksys.dto.SimpleTweetDto;
import com.cooksys.dto.TweetDto;
import com.cooksys.dto.UserAccountDto;
import com.cooksys.entity.Credentials;
import com.cooksys.entity.ReplyTweet;
import com.cooksys.entity.RepostTweet;
import com.cooksys.entity.SimpleTweet;
import com.cooksys.entity.Tweet;
import com.cooksys.entity.UserAccount;
import com.cooksys.mapper.TweetMapper;
import com.cooksys.mapper.UserMapper;
import com.cooksys.repository.TweetRepoistory;
import com.cooksys.repository.UserRepository;

@Service
public class TweetService {

	private TweetRepoistory tweetRepository;
	private TweetMapper tweetMapper;
	private UserRepository userRepository;
	private UserMapper userMapper;

	public TweetService(TweetRepoistory tweetRepoistory, TweetMapper tweetMapper, UserRepository userRepository, UserMapper userMapper)
	{
		this.tweetRepository = tweetRepoistory;
		this.tweetMapper = tweetMapper;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}
	
	public List<TweetDto> getTweets() {
		return tweetMapper.toDtos(tweetRepository.findByActiveTrueOrderByPostedDesc());
	}

	public SimpleTweetDto createTweet(ContentCredentialDto contentCredentialDto) {
		UserAccount userAccount = userRepository.findByCredentialsUsernameAndActiveTrue(contentCredentialDto.getCredentials().getUsername());
		
		// Allow user to create tweet only if they have correct password
		if (userAccount == null || 
				contentCredentialDto.getCredentials().getPassword() == null || 
				!contentCredentialDto.getCredentials().getPassword().equals(userAccount.getCredentials().getPassword()))
		{
			return null;
		}
		else
		{
			SimpleTweet tweet = new SimpleTweet();
			tweet.setAuthor(userAccount);
			tweet.setPosted(new Timestamp(System.currentTimeMillis()));
			tweet.setContent(contentCredentialDto.getContent());
			tweet.setActive(true);
			tweetRepository.save(tweet);
			return tweetMapper.toDtoSimple(tweet);
		}
	}

	public TweetDto getTweet(Integer id) {
		return tweetMapper.toDto(tweetRepository.findByIdAndActiveTrue(id));
	}

	public TweetDto deleteTweet(Integer id, Credentials credentials) {
		Tweet tweet = tweetRepository.findByIdAndActiveTrue(id);
		
		// Allow user to delete tweet only if they match author credentials
		if (tweet == null ||
				credentials.getPassword() == null || credentials.getUsername() == null ||
				!credentials.getPassword().equals(tweet.getAuthor().getCredentials().getPassword()) || 
				!credentials.getUsername().equals(tweet.getAuthor().getCredentials().getUsername()))
		{
			return null;
		} 
		else
		{
			TweetDto tweetDto = tweetMapper.toDto(tweet);
			tweet.setActive(false);
			tweetRepository.save(tweet);
			return tweetDto;
		}
	}

	public RepostTweetDto repostTweet(Integer id, Credentials credentials) {
		UserAccount userAccount = userRepository.findByCredentialsUsernameAndActiveTrue(credentials.getUsername());
		
		Tweet tweetToRepost = tweetRepository.findByIdAndActiveTrue(id);
		
		// Allow user to repost tweet only if they have the correct password and tweet isnt deleted
		if (userAccount == null || tweetToRepost == null ||
				credentials.getPassword() == null || 
				!credentials.getPassword().equals(userAccount.getCredentials().getPassword()))
		{
			return null;
		}
		else
		{
			RepostTweet tweet = new RepostTweet();
			tweet.setAuthor(userAccount);
			tweet.setPosted(new Timestamp(System.currentTimeMillis()));
			tweet.setRepostOf(tweetToRepost);
			tweet.setActive(true);
			tweetRepository.save(tweet);
			return tweetMapper.toDtoRepost(tweet);
		}
	}

	public ReplyTweetDto replyTweet(Integer id, ContentCredentialDto contentCredentials) {
		UserAccount userAccount = userRepository.findByCredentialsUsernameAndActiveTrue(contentCredentials.getCredentials().getUsername());
		
		Tweet tweetToReply = tweetRepository.findByIdAndActiveTrue(id);
		
		// Allow user to repost tweet only if they have the correct password and tweet isnt deleted
		if (userAccount == null || tweetToReply == null ||
				contentCredentials.getCredentials().getPassword() == null || 
				!contentCredentials.getCredentials().getPassword().equals(userAccount.getCredentials().getPassword()))
		{
			return null;
		}
		else
		{
			ReplyTweet tweet = new ReplyTweet();
			tweet.setAuthor(userAccount);
			tweet.setPosted(new Timestamp(System.currentTimeMillis()));
			tweet.setContent(contentCredentials.getContent());
			tweet.setInReplyTo(tweetToReply);
			tweet.setActive(true);
			tweetRepository.save(tweet);
			return tweetMapper.toDtoReply(tweet);
		}
	}

	public boolean likeTweet(Integer id, Credentials credentials) {
		UserAccount userAccount = userRepository.findByCredentialsUsernameAndActiveTrue(credentials.getUsername());
		
		Tweet tweetToLike = tweetRepository.findByIdAndActiveTrue(id);
		
		// Allow user to repost tweet only if they have the correct password and tweet isnt deleted
		if (userAccount == null || tweetToLike == null ||
				credentials.getPassword() == null || 
				!credentials.getPassword().equals(userAccount.getCredentials().getPassword()))
		{
			return false;
		}
		else
		{
			tweetToLike.getUsersWhoLikeTweet().add(userAccount);
			tweetRepository.save(tweetToLike);
			return true;
		}
	}

	public Set<UserAccountDto> getUsersWhoLiked(Integer id) {
		Tweet tweetLiked = tweetRepository.findByIdAndActiveTrue(id);
		
		if (tweetLiked == null)
		{
			return null;
		}
		else
		{
			return userMapper.toDtoSet(tweetLiked.getUsersWhoLikeTweet());
		}
	}

}