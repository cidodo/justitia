import { message } from 'antd';
import { getOrderer, addOrderer, updateOrderer, deleteOrderer } from '@/services/api';

export default {
  namespace: 'ordererManager',

  state: {
    ordererList: [],
    updateList: {
      tlsEnable: true,
      doubleVerify: true,
      systemChainId: 'testchainid',
    },
    isLoading: false,
    isFetching: false,
    isUpdate: false,
  },

  effects: {
    *handleGetOrdererList({ }, { call, put }) {
      yield put({ type: 'fetch_ordererList' })
      const res = yield call(getOrderer);

      try {
        const { data } = res;
        yield put({
          type: 'ordererList',
          payload: {
            ordererList: data
          },
        });
      } catch (error) {
        yield put({ type: 'failed_ordererList' });
      }
    },
    *handleSetOrderer({ payload }, { call, put }) {
      yield put({ type: 'fetch_addOrderer' });
      const res = yield call(addOrderer, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'addOrderer' });
          yield put({ type: 'handleGetOrdererList' });
          message.success('Orderer节点配置成功');
        } else {
          yield put({ type: 'failed_addOrderer' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_addOrderer' });
      }

    },
    *handleDeleteOrderer({ payload }, { call, put }) {
      yield put({ type: 'fetch_delOrderer' });
      const res = yield call(deleteOrderer, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'handleGetOrdererList' });
          message.success('Orderer节点删除成功');
        } else {
          yield put({ type: 'failed_delOrderer' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_delOrderer' });
      }
    },
    *handleUpdateOrderer({ payload }, { call, put }) {
      yield put({ type: 'fetch_updateOrderer' });
      const res = yield call(updateOrderer, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'updateOrderer' });
          yield put({ type: 'handleGetOrdererList' });
          message.success('Orderer节点更新成功');
        } else {
          yield put({ type: 'failed_updateOrderer' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_updateOrderer' });
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
    *handlecleanordererList({ }, { call, put }) {
      yield put({
        type: 'clean_list',
      });
    },
    *handleResetUpdate({ }, { call, put }) {
      yield put({
        type: 'reset_update',
      });
    }
  },

  reducers: {
    fetch_ordererList(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    ordererList(state, { payload }) {
      return {
        ...state,
        isLoading: false,
        ordererList: payload.ordererList
      };
    },
    failed_ordererList(state) {
      return {
        ...state,
        isLoading: false,
      }
    },
    fetch_addOrderer(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    addOrderer(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_addOrderer(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_updateOrderer(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    updateOrderer(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_updateOrderer(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_delOrderer(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    delOrderer(state) {
      return {
        ...state,
        isLoading: false,
      };
    },
    failed_delOrderer(state) {
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
