import { QueryClient } from '@tanstack/react-query';

/**
 * React Query Client configuration defining global settings for query and mutation caching.
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, 
      retry: 2,
      refetchOnWindowFocus: false,
    },
  },
});
