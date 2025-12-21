import { reactive } from 'vue';
import { getMyProfile } from '@/api/member';

export function useUserProfile() {
  const userProfile = reactive({
    name: '나',
    avatarUrl: '',
  });

  const loadUserProfile = async () => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try {
        const parsed = JSON.parse(storedUser);
        userProfile.name = parsed.nickname || parsed.name || userProfile.name;
        userProfile.avatarUrl = parsed.imageUrl || userProfile.avatarUrl;
      } catch (e) {
        console.error(e);
      }
    }

    try {
      const { data } = await getMyProfile();
      if (data?.success && data?.data) {
        const serverUser = data.data;
        userProfile.name = serverUser.nickname || serverUser.name || userProfile.name;
        userProfile.avatarUrl = serverUser.imageUrl || userProfile.avatarUrl;
      }
    } catch (e) {
      console.error(e);
    }
  };

  return { userProfile, loadUserProfile };
}
