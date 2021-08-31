/*
 * Copyright (c) 1998-2015 NetSuite, Inc.
 * 2955 Campus Drive, Suite 100, San Mateo, CA, USA 94403-2511
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * NetSuite, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NetSuite.
 */

/*
 * This file allows one to log into NetSuite and execute a variety of web service
 * operations. The only operation that exists initially is a simple get request.
 */
package com.netsuite.suitetalkcourse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.SOAPHeaderElement;

import com.netsuite.webservices.platform.core_2017_2.DataCenterUrls;
import com.netsuite.webservices.platform.core_2017_2.Passport;
import com.netsuite.webservices.platform.core_2017_2.RecordRef;
import com.netsuite.webservices.platform.core_2017_2.TokenPassport;
import com.netsuite.webservices.platform.core_2017_2.types.RecordType;
import com.netsuite.webservices.platform.faults_2017_2.ExceededRecordCountFault;
import com.netsuite.webservices.platform.faults_2017_2.ExceededRequestSizeFault;
import com.netsuite.webservices.platform.faults_2017_2.ExceededUsageLimitFault;
import com.netsuite.webservices.platform.faults_2017_2.InsufficientPermissionFault;
import com.netsuite.webservices.platform.faults_2017_2.InvalidCredentialsFault;
import com.netsuite.webservices.platform.faults_2017_2.InvalidSessionFault;
import com.netsuite.webservices.platform.faults_2017_2.UnexpectedErrorFault;
import com.netsuite.webservices.platform.messages_2017_2.ApplicationInfo;
import com.netsuite.webservices.platform.messages_2017_2.ReadResponse;
import com.netsuite.webservices.platform_2017_2.NetSuiteBindingStub;
import com.netsuite.webservices.platform_2017_2.NetSuitePortType;
import com.netsuite.webservices.platform_2017_2.NetSuiteServiceLocator;

/**
 * This is the main class for the SuiteTalk course. All of the web service
 * operations are to be added in this class.
 */
public class SuiteTalkCourse {

	// Passport info
	String netsuiteApplicationId = "";
	String netsuiteEmail = "";
	String netsuitePassword = "";
	String netsuiteAccountNumber = "tstdrv2469070";
	String netsuiteRoleId = "";
	String netsuiteAccountVersion = "2017_2";
	String netsuiteConsumerKey = "c6b95f1280b6ae76b38c21270edb77750b248a89ceaa51507496ca3d14211373";
	String netsuiteConsumerSecret = "65619ca1d49406f2360e7f1e23a0fd4da0762107d664c8f4cfc9b1dd8c322983";
	String netsuiteTokenId = "705372c5f7260422e592010735347620dd732b356b8f545dc70ea04ba359effc";
	String netsuiteTokenSecret = "284718823f8d80a5acd1b8dc9ab4f68f974f4b7c1c99b67470ca9b76873d2537";

	/*
	 * Proxy object that abstracts the communication with the NetSuite Web
	 * Services. All NetSuite operations are invoked as methods of this class.
	 */
	private NetSuitePortType service;

	Passport passport;

	/**
	 * Constructor. Sets up the NetSuite service
	 * 
	 * @throws ServiceException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SOAPException
	 */
	public SuiteTalkCourse() throws ServiceException, FileNotFoundException, IOException, SOAPException {
		// In order to use SSL forwarding for SOAP messages. Refer to FAQ for
		// details
		System.setProperty("axis.socketSecureFactory", "org.apache.axis.components.net.SunFakeTrustSocketFactory");

		// Locate the NetSuite web service.
		NetSuiteServiceLocator serviceLocator = new NetSuiteServiceLocator();

		// Get the service port based on the web services domain used when the
		// proxy classes were generated
		// This will be overridden during execution of getDataCenterURLs method
		// below
		service = serviceLocator.getNetSuitePort();

		// Setting client timeout to 2 hours for long running operations
		((NetSuiteBindingStub) service).setTimeout(1000 * 60 * 60 * 2);

		// Added to make sure the application works regardless of the data
		// center
		// setDynamicDataCenterUrl(serviceLocator, netsuiteAccountNumber);

		setPassport(); // set request-level login in the SOAP header
		// setTokenPassport(); // Comment out the setPassport() call and
		// uncomment this to use token for logging in.
	}

	/**
	 * Main method. This is the default entry point. Processing begins here.
	 * 
	 * @param args
	 * @throws SOAPException
	 */
	public static void main(String args[]) throws SOAPException {
		SuiteTalkCourse ns = null;

		// Instantiate the NetSuite web services
		try {
			// Creates an instance of the class
			ns = new SuiteTalkCourse();

		} catch (ServiceException ex) {
			System.out.println("\n\n[Error]: Error in locating the NetSuite web services. " + ex.getMessage());

		} catch (IOException ex) {
			System.out.println("\n\n[Error]: An IO error has occured. " + ex.getMessage());
		}

		/*
		 * A try-catch block is set up with appropriate exception handling for
		 * various .Net specific system and web service level exceptions. As the
		 * course progresses you will find that many NetSuite web service errors
		 * are captured in a status object, and do not generate exceptions.
		 */
		try {

			// execute methods calling web service operations here
			ns.getVendor();

         System.out.println("\nWeb Service Operation Complete\n");

		} catch (ExceededRecordCountFault e) {
			System.out.println("Excceded the maximum allowed number of records" + e.getMessage());
			System.out.println("    [Fault Code]: " + e.getMessage());

		} catch (ExceededUsageLimitFault e) {
			System.out.println("Exceeded usage limit. " + e.getMessage());
			System.out.println("    [Fault Code]: " + e.getFaultCode());

		} catch (InsufficientPermissionFault e) {
			System.out.println("You do not have sufficent permission for this request. " + e.getMessage());
			System.out.println("    [Fault Code]: " + e.getFaultCode());

		} catch (InvalidSessionFault e) {
			System.out.println("\nInvalid Session. " + e.getMessage());
			System.out.println("   [Fault Code]: " + e.getFaultCode());

		} catch (SOAPFaultException e) {
			System.out.println("There was an error processing this request. " + e.getMessage());
			System.out.println("   [Fault Code]: " + e.getFaultCode());
			System.out.println("   [Fault String]: " + e.getFaultString());
			System.out.println("   [Fault Actor]: " + e.getFaultActor());
			System.out.println("   [Fault Detail]: " + e.getDetail());

		} catch (RemoteException e) {
			System.out.println("\nRemoteException: " + e.getClass().getSimpleName());
			if (e instanceof InvalidCredentialsFault) {
				InvalidCredentialsFault invCredFault = (InvalidCredentialsFault) e;
				System.out.println("Message        : " + invCredFault.getMessage1());
			} else {
				System.out.println("Message        : " + e.getMessage());
			}
		}
	}

	/**
	 * Sample get operation call.
	 * 
	 * @throws RemoteException
	 */
	public void getVendor() throws RemoteException {
		RecordRef recordRef = new RecordRef();

		recordRef.setInternalId("201");
		recordRef.setType(RecordType.vendor);

		ReadResponse readResponse = service.get(recordRef);
		System.out.println("\nWeb Service Operation Executed\n");
	}

	/**
	 * This method is optional if you're already using the WSDL from the same
	 * domain where your account is located. Problem arises when the account is
	 * moved to a different data center. By running this method, the application
	 * would work regardless of the data center where the account is located.
	 * 
	 * @param serviceLocator
	 * @param accountId
	 * 
	 * @throws InsufficientPermissionFault
	 * @throws InvalidCredentialsFault
	 * @throws ExceededRequestSizeFault
	 * @throws UnexpectedErrorFault
	 * @throws InvalidSessionFault
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unused")
	private void setDynamicDataCenterUrl(NetSuiteServiceLocator serviceLocator, String accountId)
			throws InsufficientPermissionFault, InvalidCredentialsFault, ExceededRequestSizeFault, UnexpectedErrorFault,
			InvalidSessionFault, RemoteException, MalformedURLException, ServiceException {

		DataCenterUrls dataCenterUrls = service.getDataCenterUrls(accountId).getDataCenterUrls();
		String webServiceUrl = dataCenterUrls.getWebservicesDomain();

		// Uncomment to print out the URLs from the request
		// System.out.println(dataCenterUrls.getRestDomain());
		// System.out.println(dataCenterUrls.getSystemDomain());
		// System.out.println(dataCenterUrls.getWebservicesDomain());

		// Set the Data Center URL where the account is located
		service = serviceLocator.getNetSuitePort(
				new URL(webServiceUrl + "/services/NetSuitePort_" + netsuiteAccountVersion + "?c=" + accountId));
	}

	/**
	 * Sets the authentication information used by each request
	 * 
	 * @throws SOAPException
	 */
	private void setPassport() throws SOAPException {
		Passport passport = new Passport();
		RecordRef roleRef = new RecordRef();
		ApplicationInfo appInfo = new ApplicationInfo();

		// Replace text in the next lines as noted
		appInfo.setApplicationId(netsuiteApplicationId);
		passport.setEmail(netsuiteEmail);
		passport.setPassword(netsuitePassword);
		passport.setAccount(netsuiteAccountNumber);
		roleRef.setInternalId(netsuiteRoleId);
		passport.setRole(roleRef);

		// Display the login information in the console
		System.out.println("Login info...");
		System.out.println(String.format("    Email           : %s", passport.getEmail()));
		System.out.println(String.format("    Role Internal Id: %s", roleRef.getInternalId()));
		System.out.println(String.format("    Account         : %s", passport.getAccount()));
		System.out.println(String.format("    Application ID  : %s", appInfo.getApplicationId()));

		// Add the passport and app id to the request header
		includeInHeader("passport", passport, service);
		includeInHeader("applicationInfo", appInfo, service);

		this.passport = passport;
	}

	/**
	 * This provides an alternative way of authentication by using tokens
	 * instead of username and password combinations
	 * 
	 * @throws SOAPException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unused")
	private void setTokenPassport() throws SOAPException, UnsupportedEncodingException {
		String accountId = netsuiteAccountNumber;
		String consumerKey = netsuiteConsumerKey;
		String consumerSecret = netsuiteConsumerSecret;
		String tokenId = netsuiteTokenId;
		String tokenSecret = netsuiteTokenSecret;

		TokenPassport tokenPassport = WsHelper.generateTokenPassport(accountId, consumerKey, consumerSecret, tokenId,
				tokenSecret);

		includeInHeader("tokenPassport", tokenPassport, service);
	}

	/**
	 * Adds header objects to the request. This is used to set the web service
	 * and search preference from the code. If the specified head already
	 * exists, the value is replaced with the new entry. Otherwise, it's added
	 * to the request.
	 * 
	 * @param headerName
	 *            Either "preference" or "searchpreference".
	 * @param headerValue
	 *            Preference or SearchPreference object depending on what you're
	 *            trying to add.
	 * @param service
	 *            Service that's used in the request.
	 * 
	 * @throws SOAPException
	 */
	public static void includeInHeader(String headerName, Object headerValue, NetSuitePortType service)
			throws SOAPException {
		String namespace = "urn:messages.platform.webservices.netsuite.com";

		// Cast your login NetSuitePortType variable to a NetSuiteBindingStub
		NetSuiteBindingStub binding = (NetSuiteBindingStub) service;

		// Get the current list of headers
		ArrayList<SOAPHeaderElement> headerList = new ArrayList<SOAPHeaderElement>(Arrays.asList(binding.getHeaders()));

		// Clear the headers as they do not overwrite when you are using
		// Axis/Java
		binding.clearHeaders();

		// Remove the header that you're adding if it already exists.
		Iterator<SOAPHeaderElement> itr = headerList.iterator();
		while (itr.hasNext()) {
			SOAPHeaderElement header = (SOAPHeaderElement) itr.next();

			if (headerName == header.getName()) {
				itr.remove();
				break;
			}
		}

		// Put the headers back to the service
		for (SOAPHeaderElement header : headerList) {
			binding.setHeader(header);
		}

		// Add the current header to the service.
		binding.setHeader(namespace, headerName, headerValue);
	}
}