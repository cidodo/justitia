import { message } from 'antd';
import { getFabricUser, addFabricUser, updateFabricUser, deleteFabricUser } from '@/services/api';

export default {
  namespace: 'fabricUserManager',

  state: {
    userList: [],
    updateList: {
      tlsEnable: true,
      admin: true,
    },
    isLoading: false,
    isFetching: false,
    isUpdate: false,
  },

  effects: {
    *handleGetUserList({ }, { call, put }) {
      console.log('get')
      yield put({ type: 'fetch_userList' })
      const res = yield call(getFabricUser);

      try {
        const { data } = res;
        yield put({
          type: 'userList',
          payload: {
            userList: data
          },
        });
      } catch (error) {
        yield put({ type: 'failed_userList' });
      }
    },
    *handleSetUser({ payload }, { call, put }) {
      yield put({ type: 'fetch_addUser' });
      const res = yield call(addFabricUser, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'addUser' });
          yield put({ type: 'handleGetUserList' });
          message.success('新增用户成功');
        } else {
          yield put({ type: 'failed_addUser' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_addUser' });
      }

    },
    *handleDeleteUser({ payload }, { call, put }) {
      yield put({ type: 'fetch_delUser' });
      const res = yield call(deleteFabricUser, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'handleGetUserList' });
          message.success('删除用户成功');
        } else {
          yield put({ type: 'failed_delUser' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_delUser' });
      }
    },
    *handleUpdateUser({ payload }, { call, put }) {
      yield put({ type: 'fetch_updateUser' });
      const res = yield call(updateFabricUser, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'updateUser' });
          yield put({ type: 'handleGetUserList' });
          message.success('用户信息更新成功');
        } else {
          yield put({ type: 'failed_updateUser' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_updateUser' });
      }
    },
    *handleGetUpdateList({ payload }, { call, put }) {
      yield put({ 
        type: 'get_update_list',
        payload: {
          data: payload
        }
      });
    },
    *handlecleanUserList({}, { call, put }) {
      yield put({ 
        type: 'clean_list',
      });
    },
    *handleResetUpdate({}, { call, put }) {
      yield put({ 
        type: 'reset_update',
      });
    }
  },

  reducers: {
    fetch_userList(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    userList(state, { payload }) {
      return {
        ...state,
        isLoading: false,
        userList: payload.userList
      };
    },
    failed_userList(state) {
      return {
        ...state,
        isLoading: false,
      }
    },
    fetch_addUser(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    addUser(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_addUser(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_updateUser(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    updateUser(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_updateUser(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_delUser(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    delUser(state) {
      return {
        ...state,
        isLoading: false,
      };
    },
    failed_delUser(state) {
      return {
        ...state,
        isLoading: false,
      }
    },
    get_update_list(state, { payload }) {
      return {
        ...state,
        isUpdate: true,
        updateList: payload.data
      }
    },
    clean_list(state) {
      return {
        ...state,
        updateList: {
          tlsEnable: true,
          admin: true
        }
      }
    },
    reset_update(state) {
      return {
        ...state,
        isUpdate: false,
        updateList: {
          tlsEnable: true,
          admin: true,
        }
      }
    }
  },

  subscriptions: {
    setup({ history }) {
      // Subscribe history(url) change, trigger `load` action if pathname is `/`
      return history.listen(({ pathname, search }) => {
        if (typeof window.ga !== 'undefined') {
          window.ga('send', 'pageview', pathname + search);
        }
      });
    },
  },
};
