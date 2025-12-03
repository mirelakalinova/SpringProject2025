package com.example.mkalinova.init;

import com.example.mkalinova.app.exepction.GlobalExceptionHandler;
import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class BlockedUserFilter extends OncePerRequestFilter {
	private final UserRepository userRepository;
	
	public BlockedUserFilter(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		return path.equals("/access-denied") || path.equals("/login") || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/");
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
			String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
			User user = userRepository.findByUsername(username);
			if (user != null && !user.isEnabled()) {
				SecurityContextHolder.clearContext();
				request.setAttribute("heading", "Достъпът е отказан!");
				request.setAttribute("message", "Вашият акаунт е блокиран.");
				request.setAttribute("image", "../../images/errors/403.jpg");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				
				request.getRequestDispatcher("/access-denied").forward(request, response);
				return;
			}
		}
		filterChain.doFilter(request, response);
	
	}
}
