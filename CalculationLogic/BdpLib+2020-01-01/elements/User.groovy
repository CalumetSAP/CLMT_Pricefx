import com.googlecode.genericdao.search.Filter

/**
 * @param groupName
 * @return a list of user that belong to the group name
 * <br><b>Note:</b> user should:
 * 		<li>have an Email</li>
 * 		<li>be Active</li>
 * 		<li>be workflowEmailingActivated</li>
 */
List<Object> findUsersInGroup(groupName) {
	return api.find("U", 0, api.getMaxFindResultsLimit(), null,
			Filter.and(
					Filter.isNotNull("email"),
					Filter.equal("activated", true),
					Filter.equal("workflowEmailingActivated", true),
					Filter.some("groups", Filter.equal("uniqueName", groupName))
			)
	)
}

boolean isCurrentUserInGroup(String groupName) {
	def currentUser = api.user()
	return api.isUserInGroup(groupName, currentUser.loginName as String)
}
