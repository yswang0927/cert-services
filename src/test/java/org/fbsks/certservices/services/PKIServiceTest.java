package org.fbsks.certservices.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;
import org.fbsks.certservices.model.IdentityContainer;
import org.fbsks.certservices.model.PKI;
import org.fbsks.certservices.repository.PKIRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author fabio.resner
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PKIServiceTest {

	@Autowired
	private PKIService pkiService;
	
	@Autowired
	private PKIRepository pkiRepository; 
	
	private static final String TEST_PKI_NAME = "TESTPKI";
	private static final String TEST_FINAL_USER_CERT_NAME = "TestFinalUser";
	private static final String NON_EXISITING_CA_NAME = "Hi im a ca and I don't exist";
	
	@Test
	public void shouldGeneratePKIOnlyWithRootCA() {
		this.pkiService.generatePKI(TEST_PKI_NAME);
		PKI pki = pkiRepository.findOneByName(TEST_PKI_NAME);
		
		assertEquals(TEST_PKI_NAME, pki.getName());
		assertNotNull(pki);
		assertEquals(true, pki.getCas().size() > 0);
	}
	
	@Test
	public void shouldListPKIsCorrectly() {
		List<PKI> pkis = this.pkiService.listPKIs();
		
		assertEquals(0, pkis.size());
		
		this.pkiService.generatePKI(TEST_PKI_NAME);
		pkis = this.pkiService.listPKIs();
		
		assertEquals(1, pkis.size());
		assertEquals(TEST_PKI_NAME, pkis.get(0).getName());
	}
	
	@Test
	public void shouldGenerateFinalUserCertificateOnExistingCA() {
		PKI pki = this.pkiService.generatePKI(TEST_PKI_NAME);
		
		IdentityContainer finalUserCertificate = this.pkiService.generateIdentity(pki.getName(), TEST_FINAL_USER_CERT_NAME);
		
		assertEquals(finalUserCertificate.getCertificate().getIssuer(), pki.getCas().get(0).getIdentityContainer().getCertificate().getSubject());
	}
	
	@Test
	public void shouldGetCACertificate() {
		PKI pki = this.pkiService.generatePKI(TEST_PKI_NAME);
		
		X509CertificateHolder caCert = this.pkiService.getCertificateChain(TEST_PKI_NAME);
		X509CertificateHolder retrievedCaCert = pki.getCas().get(0).getIdentityContainer().getCertificate();
		
		assertEquals(caCert, retrievedCaCert);
	}
	
	@Test(expected=RuntimeException.class)
	public void shouldFailtGetCACertificate() {
		this.pkiService.getCertificateChain(NON_EXISITING_CA_NAME);
	}
}
