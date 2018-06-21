package nam.kwan.woo;

import java.awt.image.BufferedImage;
import java.util.*;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;

public class InstagramModule {
	/*
	 * final String userId = "jojem40@gmail.com"; final String passWord = "jj5649";
	 * final String userName = "suhwan7155";
	 */

	List<BufferedImage> followerBufferedImageProfile;
	List<String> followerUserName;

	Instagram4j instagram;
	InstagramUser user;
	InstagramGetUserFollowersResult githubFollowers;
	List<InstagramUserSummary> users;

	boolean isSet;

	private boolean isValid() {
		return this.isSet;
	}

	public boolean initializeInstagramModule(String userId, String passWord, String userName) {
		append(userName + " 계정으로 로그인 시도");

		try {
			instagram = Instagram4j.builder().username(userId).password(passWord).build();
			instagram.setup();
			instagram.login();
			append("로그인 성공");
			InstagramSearchUsernameResult result = instagram.sendRequest(new InstagramSearchUsernameRequest(userName));
			user = result.getUser();

			append("팔로우 : " + user.getFollowing_count());
			append("팔로잉 : " + user.getFollower_count());
			append("프로필 url : " + user.getHd_profile_pic_url_info().getUrl());// 계정 소유자의 프로필 사진 주소 나열.

			githubFollowers = instagram.sendRequest(new InstagramGetUserFollowersRequest(user.getPk()));
			users = githubFollowers.getUsers();

			System.out.println("UserSummary size: " + users.size());
			isSet = true;

			return isValid();
		} catch (Exception e) {
			isSet = false;
			return isValid();
			// TODO: handle exception
		}
	}

	public List<String> getListStringBufferUserID() {
		if (!isValid()) {
			System.out.println("[Error] Please Login Instagram");
			System.out.println("[Tips]  Call function InitializeInstagramModule");
			return null;
		}
		
		if(followerUserName == null)
			return null;
		
		return followerUserName;
	}

	public List<BufferedImage> getListByteBufferInstagramUser() {
		if (!isValid()) {
			System.out.println("[Error] Please Login Instagram");
			System.out.println("[Tips]  Call function InitializeInstagramModule");
			return null;
		}
		
		if(followerBufferedImageProfile == null)
			return null;
		
		return followerBufferedImageProfile;
	}
	
	public List<BufferedImage> parsingGivenUserName() {
		if (!isValid()) {
			System.out.println("[Error] Please Login Instagram");
			System.out.println("[Tips]  Call function InitializeInstagramModule");
			return null;
		}

		followerBufferedImageProfile = new ArrayList<BufferedImage>();
		followerUserName = new ArrayList<String>();

		try {
			int limit = 100;
			int cnt = 0;
			
			followerBufferedImageProfile.add(Converter.convertURLtoBufferedImage(user.getHd_profile_pic_url_info().getUrl()));
			followerUserName.add(user.username);
			
			for (InstagramUserSummary u : users) {
				if (cnt++ == limit)
					break;
				long startTime = System.currentTimeMillis();

				if (u == null) {
					System.out.println("null found");
					continue;
				}

				System.out.println("Iteration: " + cnt + "\tUsername: " + u.getUsername());
				long midTime = System.currentTimeMillis();
				System.out.println("\nRequest parsing time: " + (midTime - startTime) + "ms\n");

								
				 InstagramSearchUsernameResult tmp_result = instagram.sendRequest(new InstagramSearchUsernameRequest(u.getUsername()));
				// long midTime = System.currentTimeMillis();
				// System.out.println("\nRequest parsing time: " + (midTime-startTime)+"ms\n");
				 final InstagramUser tmp_user = tmp_result.getUser();
				 
				 BufferedImage retVal = Converter.convertURLtoBufferedImage(tmp_user.getProfile_pic_url());
					
				followerBufferedImageProfile.add(retVal);
				followerUserName.add(u.getUsername());
			} 

			return followerBufferedImageProfile;
		} catch (Exception e) {
			e.printStackTrace();
			append(e.toString());
			return null;
		}
	}

	private void append(final String log) {
		System.out.println(log);
	}
}