import { message } from 'antd';
import { getPeer, addPeer, updatePeer, deletePeer } from '@/services/api';

export default {
  namespace: 'peerManager',

  state: {
    peerList: [],
    updateList: {
      tlsEnable: true,
      doubleVerify: true,
    },
    isLoading: false,
    isFetching: false,
    isUpdate: false,
  },

  effects: {
    *handleGetPeerList({ }, { call, put }) {
      yield put({ type: 'fetch_peerList' })
      const res = yield call(getPeer);

      try {
        const { data } = res;
        yield put({
          type: 'peerList',
          payload: {
            peerList: data
          },
        });
      } catch (error) {
        yield put({ type: 'failed_peerList' });
      }
    },
    *handleSetPeer({ payload }, { call, put }) {
      yield put({ type: 'fetch_addPeer' });
      const res = yield call(addPeer, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'addPeer' });
          yield put({ type: 'handleGetPeerList' });
          message.success('peer节点配置成功');
        } else {
          yield put({ type: 'failed_addPeer' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_addPeer' });
      }

    },
    *handleDeletePeer({ payload }, { call, put }) {
      yield put({ type: 'fetch_delPeer' });
      const res = yield call(deletePeer, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'handleGetPeerList' });
          message.success('peer节点删除成功');
        } else {
          yield put({ type: 'failed_delPeer' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_delPeer' });
      }
    },
    *handleUpdatePeer({ payload }, { call, put }) {
      yield put({ type: 'fetch_updatePeer' });
      const res = yield call(updatePeer, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'updatePeer' });
          yield put({ type: 'handleGetPeerList' });
          message.success('peer节点更新成功');
        } else {
          yield put({ type: 'failed_updatePeer' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_updatePeer' });
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
    *handlecleanPeerList({}, { call, put }) {
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
    fetch_peerList(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    peerList(state, { payload }) {
      return {
        ...state,
        isLoading: false,
        peerList: payload.peerList
      };
    },
    failed_peerList(state) {
      return {
        ...state,
        isLoading: false,
      }
    },
    fetch_addPeer(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    addPeer(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_addPeer(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_updatePeer(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    updatePeer(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_updatePeer(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_delPeer(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    delPeer(state) {
      return {
        ...state,
        isLoading: false,
      };
    },
    failed_delPeer(state) {
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
          doubleVerify: true
        }
      }
    },
    reset_update(state) {
      return {
        ...state,
        isUpdate: false,
        updateList: {
          tlsEnable: true,
          doubleVerify: true,
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
