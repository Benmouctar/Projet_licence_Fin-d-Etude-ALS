import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationService } from '../services/notificationService';
import type { NotificationDTO } from '../types';







/**
 * Custom React hook for encapsulating and exposing state, operations, and queries related to Notifications.
 */
export const useNotifications = () => {
  return useQuery<NotificationDTO[]>({
    queryKey: ['notifications'],
    queryFn: notificationService.getMyNotifications,
    staleTime: 0,           
    refetchInterval: 15_000, 
    retry: false,
  });
};





export const useMarkNotificationRead = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, string>({
    mutationFn: (id: string) => notificationService.markRead(id),
    onMutate: async (id: string) => {
      await queryClient.cancelQueries({ queryKey: ['notifications'] });
      const previous = queryClient.getQueryData<NotificationDTO[]>(['notifications']);
      
      queryClient.setQueryData<NotificationDTO[]>(
        ['notifications'],
        (old = []) => old.map(n => n.id === id ? { ...n, read: true } : n),
      );
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) {
        queryClient.setQueryData(['notifications'], context.previous);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
    },
  });
};


export const useMarkAllNotificationsRead = () => {
  const queryClient = useQueryClient();
  return useMutation<void, Error, void>({
    mutationFn: async () => {
      const notifications = queryClient.getQueryData<NotificationDTO[]>(['notifications']) ?? [];
      await Promise.all(
        notifications.filter(n => !n.readStatus).map(n => notificationService.markRead(n.id))
      );
    },
    onSuccess: () => {
      queryClient.setQueryData<NotificationDTO[]>(
        ['notifications'],
        (old = []) => old.map(n => ({ ...n, readStatus: true }))
      );
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
    },
  });
};
