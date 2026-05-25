import { useQuery } from '@tanstack/react-query';
import { recommendationService } from '../services/recommendationService';
import type { Course } from '../types';







/**
 * Custom React hook for encapsulating and exposing state, operations, and queries related to Recommendations.
 */
export const useRecommendations = () => {
  return useQuery<Course[]>({
    queryKey: ['recommendations'],
    queryFn: recommendationService.getMyRecommendations,
    staleTime: 1000 * 60 * 10, 
    retry: false,
  });
};
