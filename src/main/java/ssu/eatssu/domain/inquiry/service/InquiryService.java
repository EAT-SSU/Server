package ssu.eatssu.domain.inquiry.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.inquiry.dto.CreateInquiryRequest;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.repository.InquiryRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

@RequiredArgsConstructor
@Service
@Transactional
public class InquiryService {

	private final UserRepository userRepository;
	private final InquiryRepository inquiryRepository;

	public Inquiry createUserInquiry(CustomUserDetails userDetails, CreateInquiryRequest request) {
		User user = userRepository.findById(userDetails.getId())
								  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

		Inquiry inquiry = new Inquiry(request.getContent(), user, request.getEmail());

		return inquiryRepository.save(inquiry);
	}

}
