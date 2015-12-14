package de.zalando.zmon.security.permission;

import com.google.common.base.Function;

import de.zalando.zmon.domain.AlertComment;
import de.zalando.zmon.security.authority.ZMonAuthority;

class HasDeleteCommentPermission implements Function<ZMonAuthority, Boolean> {

	private final AlertComment comment;

	HasDeleteCommentPermission(AlertComment comment) {
		this.comment = comment;
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasDeleteCommentPermission(comment);
	}

}
