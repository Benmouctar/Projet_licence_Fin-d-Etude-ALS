import { useQuery } from '@tanstack/react-query';
import api from '../services/api';
import type { ModuleAccessResult } from '../types';









/**
 * Custom React hook for encapsulating and exposing state, operations, and queries related to Module Access.
 */
export const useModuleAccess = (enrollmentId: string, moduleId: string) => {
  return useQuery<ModuleAccessResult>({
    queryKey: ['module-access', enrollmentId, moduleId],
    queryFn: async () => {
      const res = await api.get<ModuleAccessResult>(
        `/enrollments/${enrollmentId}/access/${moduleId}`
      );
      return res.data;
    },
    enabled: Boolean(enrollmentId) && Boolean(moduleId),
    staleTime: 30_000,
  });
};
