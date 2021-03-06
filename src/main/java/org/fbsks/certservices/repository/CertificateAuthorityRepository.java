package org.fbsks.certservices.repository;

import org.fbsks.certservices.model.CertificateAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateAuthorityRepository extends JpaRepository<CertificateAuthority, Long> {

	CertificateAuthority findOneByName(String caName);
}
