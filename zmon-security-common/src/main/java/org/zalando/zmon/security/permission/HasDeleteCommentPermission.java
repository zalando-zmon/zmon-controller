package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.AlertCommentImport;
import org.zalando.zmon.security.authority.ZMonAuthority;

import com.google.common.base.Function;

class HasDeleteCommentPermission implements Function<ZMonAuthority, Boolean> {

	private final AlertCommentImport comment;

	HasDeleteCommentPermission(AlertCommentImport comment) {
		this.comment = comment;
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasDeleteCommentPermission(comment);
	}

}
