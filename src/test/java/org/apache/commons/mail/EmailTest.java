package org.apache.commons.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Test class for EmailConcrete.
 */
public class EmailTest {
    
    // Test email addresses
    private static final String[] TEST_EMAILS = { "ab@bc.com", "a.b@c.org", "abcdefg@abcdefg.com.bd"};
    // Header constants
    private static final String HEADER_NAME = "X-Header-Test";
    private static final String HEADER_VALUE = "TestValue";
    // Valid name for email tests
    private static final String VALID_NAME = "Valid Name";
    // SMTP host value for testing
    private static final String SMTP_HOST_VALUE = "smtp.example.com";
    
    private EmailConcrete email; // Instance of EmailConcrete
    
    // Setup method to initialize EmailConcrete instance
    @Before
    public void setUpEmailTest() throws Exception {
        email = new EmailConcrete();
    }
    
    // Tear down method
    @After
    public void tearDownEmailTest() throws Exception {
        // Clean up resources if needed
    }
    
    /*
     * Test addBcc(String email) function
     */
    @Test // 84.8%
    public void testAddBcc() throws Exception {  
        email.addBcc(TEST_EMAILS);
        assertEquals(3, email.getBccAddresses().size());
    }
    
    /*
     * Test addCc(String email) function
     */
    @Test // 100%
    public void testAddCc() throws Exception {
        // Test valid case
        email.addCc(TEST_EMAILS[1]);
        assertEquals("Expected 1 Cc addresses to be added", 1, email.getCcAddresses().size());
        
        // Test empty email argument
        try {
            email.addCc();
            fail("Should have thrown EmailException on empty Cc");
        } catch (EmailException e) {
            assertEquals("Address List provided was invalid", e.getMessage());
        }
    }
    
    /*
     * Test addHeader(String name, String value)
     */
    @Test // 100%
    public void testAddHeader() throws Exception {
        // Valid header
        email.addHeader(HEADER_NAME, HEADER_VALUE);
        assertEquals("Expected header was not found or did not match the expected value.",
                HEADER_VALUE, email.headers.get(HEADER_NAME));
        
        // Test null and empty values for name and value
        testHeaderExceptions(null, HEADER_VALUE, "name can not be null or empty");
        testHeaderExceptions("", HEADER_VALUE, "name can not be null or empty");
        testHeaderExceptions(HEADER_NAME, null, "value can not be null or empty");
        testHeaderExceptions(HEADER_NAME, "", "value can not be null or empty");
    }

    // Helper method to test header exceptions
    private void testHeaderExceptions(String name, String value, String expectedMessage) {
        try {
            email.addHeader(name, value);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    
    /*
     * Test addReplyTo(String email, String name) function
     */
    @Test // 100%
    public void testAddReplyTo() throws Exception {
        // Adding reply-to addresses
        for (String testEmail : TEST_EMAILS) {
            email.addReplyTo(testEmail, VALID_NAME);
        }

        assertEquals("Number of reply-to addresses should match the number of test emails",
                TEST_EMAILS.length, email.getReplyToAddresses().size());

        // Verify that each TEST_EMAIL has been added as a reply-to address
        int index = 0;
        for (InternetAddress replyTo : email.getReplyToAddresses()) {
            assertEquals("Reply-to email address did not match", TEST_EMAILS[index], replyTo.getAddress());
            assertEquals("Reply-to name did not match", VALID_NAME, replyTo.getPersonal());
            index++;
        }
    }
    
    /*
     * Test buildMimeMessage() function
     */
    @Test // 72.1%
    public void testBuildMimeMessage() throws Exception {
        // Setting up email parameters
        email.setHostName("localhost");
        email.setSmtpPort(1234);
        email.setFrom("abcde@b.com");
        email.addTo("cfgkgm@d.com");
        email.setSubject("test mail");
        email.setCharset("ISO-8859-1");
        email.setContent("test content", "text/plain");

        // Additional parameters for testing
        email.addCc("cc@example.com");
        email.addBcc("bcc@example.com");
        email.addReplyTo("reply@example.com");
        email.addHeader("X-Custom-Header", "Value");

        // Test building MimeMessage
        email.buildMimeMessage();

        // Ensure MimeMessage is built
        assertNotNull(email.getMimeMessage());
    }

    /*
     * Test buildMimeMessage() when MimeMessage is already built
     */
    @Test(expected = IllegalStateException.class) 
    public void testBuildMimeMessageWhenAlreadyBuilt() throws Exception {
        // Setting up email parameters
        email.setHostName("localhost");
        email.setSmtpPort(1234);
        email.setFrom("abcde@b.com");
        email.addTo("cfgkgm@d.com");
        email.setSubject("test mail");
        email.setCharset("ISO-8859-1");
        email.setContent("test content", "text/plain");

        // Additional parameters for testing
        email.addCc("cc@example.com");
        email.addBcc("bcc@example.com");
        email.addReplyTo("reply@example.com");
        email.addHeader("X-Custom-Header", "Value");

        // Test building MimeMessage
        email.buildMimeMessage();

        // Attempting to build the MimeMessage again should result in an IllegalStateException being thrown
        email.buildMimeMessage();
    }

    /*
     * Test setting sentDate explicitly
     */
    @Test 
    public void testBuildMimeMessageWithSentDate() throws Exception {
        // Setting up email parameters
        email.setHostName("localhost");
        email.setSmtpPort(1234);
        email.setFrom("abcde@b.com");
        email.addTo("cfgkgm@d.com");
        email.setSubject("test mail");
        email.setCharset("ISO-8859-1");
        email.setContent("test content", "text/plain");

        // Additional parameters for testing
        email.addCc("cc@example.com");
        email.addBcc("bcc@example.com");
        email.addReplyTo("reply@example.com");
        email.addHeader("X-Custom-Header", "Value");

        // Test setting sentDate explicitly
        Date customSentDate = new Date();
        email.setSentDate(customSentDate);
        email.buildMimeMessage();
     // Assert
        long tolerance = 1000; // 1 second tolerance
        assertTrue(Math.abs(customSentDate.getTime() - email.getMimeMessage().getSentDate().getTime()) <= tolerance);

    }

    /*
     * Test buildMimeMessage() with HTML content
     */
    @Test 
    public void testBuildMimeMessageWithHtmlContent() throws Exception {
        // Setting up email parameters
        email.setHostName("localhost");
        email.setSmtpPort(1234);
        email.setFrom("abcde@b.com");
        email.addTo("cfgkgm@d.com");
        email.setSubject("test mail");
        email.setCharset("ISO-8859-1");
        email.setContent("<html><body><h1>HTML Content</h1></body></html>", "text/html");

        // Test building MimeMessage
        email.buildMimeMessage();

        // Ensure MimeMessage is built
        assertNotNull(email.getMimeMessage());
    }
    
    @Test(expected = EmailException.class)
    public void testBuildMimeMessageWithoutFromAddress() throws Exception {
        // Create an instance of EmailConcrete
        EmailConcrete email = new EmailConcrete();

        // Set up other required parameters for MimeMessage construction
        email.setHostName("localhost");
        email.setSmtpPort(1234);
        email.addTo("recipient@example.com");
        email.setSubject("Test email without from address");
        email.setCharset("UTF-8");
        email.setContent("Test content without from address", "text/plain");

        // Attempt to build MimeMessage without setting the from address
        email.buildMimeMessage();

        // If the from address is not set, an EmailException should be thrown
        fail("Expected EmailException for missing from address");
    }

    /*
     * Test getHostName throws Exception
     */
    @Test // 70.6%
    public void testGetHostNameWhenSessionIsNotNull() throws Exception {
        // Act
        String hostName = email.getHostName();

        // Assert
        assertNotEquals(SMTP_HOST_VALUE, hostName);
    }

    @Test
    public void testGetHostNameWhenSessionIsNullAndHostNameIsNotEmpty() throws Exception {
        email.setHostName(SMTP_HOST_VALUE);

        // Act
        String hostName = email.getHostName();

        // Assert
        assertEquals(SMTP_HOST_VALUE, hostName);
    }

    @Test
    public void testGetHostNameWhenBothSessionAndHostNameAreNull() throws Exception {
        // Act
        String hostName = email.getHostName();

        // Assert
        assertEquals(null, hostName);
    }
    
    /*
     * Test getMailSession() function
     */
    @Test(expected = EmailException.class) // 72%
    public void testGetMailSession() throws Exception {
        // Act
        Session session = email.getMailSession();
        assertNotNull("Expected an EmailException to be thrown", session);
    }
    
    /*
     * Test getSentDate() function
     */
    @Test // 100%
    public void testGetSentDate() throws Exception {
        // When sentDate is null
        email.setSentDate(null);
    }
    
    @Test
    public void testGetSentDateWhenSentDateIsNotNull() throws Exception {
        // Arrange
        email = new EmailConcrete();
        Date originalSentDate = new Date();
        email.setSentDate(originalSentDate);

        // Act
        Date sentDate = email.getSentDate();
        // Assert
        assertNotNull(sentDate);
        assertEquals(originalSentDate, sentDate);
    }
    
    /*
     * Test getSocketConnectionTimeout() function
     */
    @Test // 100%
    public void testGetSocketConnectionTimeout() throws Exception {
        // Assuming socketConnectionTimeout is set
        email.setSocketConnectionTimeout(10000);
        assertEquals(10000, email.getSocketConnectionTimeout());
    }
    
    /*
     * Test setFrom(String email) function
     */
    @Test // 100%
    public void testSetFrom() throws Exception {
        // Test valid email
        String validEmail = "test@example.com";
        email.setFrom(validEmail);
        assertEquals(validEmail, email.getFromAddress().getAddress());
    }
}

