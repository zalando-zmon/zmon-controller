package org.zalando.zmon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.zalando.zmon.config.FeedbackProperties;
import org.zalando.zmon.domain.Feedback;
import org.zalando.zmon.service.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackProperties feedbackProperties;

    @Autowired
    private FeedbackServiceImpl(final FeedbackProperties feedbackProperties) {
        this.feedbackProperties = feedbackProperties;
    }

    @Override
    public Feedback getFeedback() {
        return new Feedback(this.feedbackProperties.getUrl());
    }

}
