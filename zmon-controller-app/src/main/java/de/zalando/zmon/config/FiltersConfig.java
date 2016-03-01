package de.zalando.zmon.config;

//@Configuration
public class FiltersConfig {

    // @Bean
    // public MySimpleFilter mySimpleFilter() {
    // return new MySimpleFilter();
    // }
    //
    // @Order(Ordered.HIGHEST_PRECEDENCE + 100)
    // static class MySimpleFilter extends OncePerRequestFilter {
    //
    // private final Logger log = LoggerFactory.getLogger(MySimpleFilter.class);
    //
    // @Override
    // protected void doFilterInternal(HttpServletRequest request,
    // HttpServletResponse response,
    // FilterChain filterChain) throws ServletException, IOException {
    //
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // if (authentication == null) {
    // log.info("authentication is null");
    // } else {
    // log.info("got an authentication");
    // }
    // filterChain.doFilter(request, response);
    // }
    //
    // }
}
