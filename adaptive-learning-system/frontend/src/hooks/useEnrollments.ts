import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { enrollmentService } from '../services/enrollmentService';

/**
 * Custom React hook for encapsulating and exposing state, operations, and queries related to Enrollments.
 */
export const useEnrollments = () => {
  return useQuery({
    queryKey: ['enrollments'],
    queryFn: () => enrollmentService.getMyEnrollments(),
  });
};

export const useEnroll = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (courseId: string) => enrollmentService.enroll(courseId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['enrollments'] });
    },
  });
};

export const useCompleteModule = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ enrollmentId, moduleId, score, threshold }: { enrollmentId: string; moduleId: string; score?: number; threshold?: number }) =>
      enrollmentService.completeModule(enrollmentId, moduleId, score, threshold),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['enrollments'] });
      
      
      queryClient.invalidateQueries({ queryKey: ['module-access'] });
    },
  });
};

export const useUnenroll = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (courseId: string) => enrollmentService.unenroll(courseId),
    onSuccess: () => {
      
      queryClient.invalidateQueries({ queryKey: ['enrollments'] });
    },
  });
};
