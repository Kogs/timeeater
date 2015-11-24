/**
 *
 */
package de.kogs.timeeater.data.hooks;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 */
public class JiraHook {
	
	private String issuesPattern;
	private JiraRestClient restClient;
	private HttpClient httpClient;
	

	public static void main(String[] args) throws URISyntaxException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("User:");
		String userName = scanner.nextLine();
		
		System.out.println("PW:");
		String pw = new String(scanner.nextLine());
		
		new JiraHook("https://issues.proemion.com/", userName, pw);
	}
	
	/**
	 * @throws URISyntaxException
	 */
	public JiraHook (String serverURL, String user, String password) throws URISyntaxException {
		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		
		URI serverURI = new URI(serverURL);
		
		httpClient = new AsynchronousHttpClientFactory().createClient(serverURI,
				new BasicHttpAuthenticationHandler(user, password));
		restClient = factory.createWithBasicHttpAuthentication(serverURI, user, password);
		
		System.out.println(getIssue("FDAQA-1060").getAssignee());
	}
	
	private Issue getIssue(String issueName) {
		Promise<Issue> issue = restClient.getIssueClient().getIssue(issueName);
		
		try {
			return issue.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
