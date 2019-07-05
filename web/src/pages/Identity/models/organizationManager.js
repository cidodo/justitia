import { message } from 'antd';
import { getOrganiztion, addOrganiztion, updateOrganiztion, deleteOrganiztion, createDbcMember } from '@/services/api';

export default {
  namespace: 'organizationManager',

  state: {
    orgList: [],
    updateList: {
      tlsEnable: true,
      doubleVerify: true,
      type: 'PEER_ORGANIZATION'
    },
    isLoading: false,
    isFetching: false,
    isUpdate: false,
    isCreate: false,
    memberFile: null,
  },

  effects: {
    *handleGetOrgList({ }, { call, put }) {
      yield put({ type: 'fetch_orgList' })
      const res = yield call(getOrganiztion);

      try {
        const { data } = res;
        yield put({
          type: 'orgList',
          payload: {
            orgList: data
          },
        });
      } catch (error) {
        yield put({ type: 'failed_orgList' });
      }
    },
    *handleSetOrg({ payload }, { call, put }) {
      yield put({ type: 'fetch_addOrg' });
      const res = yield call(addOrganiztion, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'addOrg' });
          yield put({ type: 'handleGetOrgList' });
          message.success('新增组织成功');
        } else {
          yield put({ type: 'failed_addOrg' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_addOrg' });
      }

    },
    *handleDeleteOrg({ payload }, { call, put }) {
      yield put({ type: 'fetch_delOrg' });
      const res = yield call(deleteOrganiztion, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'handleGetOrgList' });
          message.success('删除组织成功');
        } else {
          yield put({ type: 'failed_delOrg' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_delOrg' });
      }
    },
    *handleUpdateOrg({ payload }, { call, put }) {
      yield put({ type: 'fetch_updateOrg' });
      const res = yield call(updateOrganiztion, payload);
      try {
        const { code, msg } = res;
        if (code === 0) {
          yield put({ type: 'updateOrg' });
          yield put({ type: 'handleGetOrgList' });
          message.success('更新组织成功');
        } else {
          yield put({ type: 'failed_updateOrg' });
          message.error(msg);
        }
      } catch (error) {
        yield put({ type: 'failed_updateOrg' });
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
    *handlecleanOrgList({}, { call, put }) {
      yield put({ 
        type: 'clean_list',
      });
    },
    *handleResetUpdate({}, { call, put }) {
      yield put({ 
        type: 'reset_update',
      });
    },
    // 生成证书
    *handleCreateMember({ payload }, { call, put }) {
      yield put({ type: 'fetch_createMember' });
      try {
        const response = yield call(createDbcMember, payload);
        if (response.code && response.code !== 0) {
          message.error(response.msg);
          yield put({ type: 'err_createMember' });
        } else {
          message.success("生成证书成功");
          yield put({
            type: 'createMember',
            payload: response
          });
          yield put({ type: 'handleGetOrgList' });
        }        
      } catch (error) {
        yield put({ type: 'err_createMember' });
      }
    },
  },

  reducers: {
    fetch_orgList(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    orgList(state, { payload }) {
      return {
        ...state,
        isLoading: false,
        orgList: payload.orgList ? [payload.orgList] : []
      };
    },
    failed_orgList(state) {
      return {
        ...state,
        isLoading: false,
      }
    },
    fetch_addOrg(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    addOrg(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_addOrg(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_updateOrg(state) {
      return {
        ...state,
        isFetching: true,
      }
    },
    updateOrg(state) {
      return {
        ...state,
        isFetching: false,
      };
    },
    failed_updateOrg(state) {
      return {
        ...state,
        isFetching: false,
      }
    },
    fetch_delOrg(state) {
      return {
        ...state,
        isLoading: true,
      }
    },
    delOrg(state) {
      return {
        ...state,
        isLoading: false,
      };
    },
    failed_delOrg(state) {
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
    },
    fetch_createMember(state) {
      return {
        ...state,
        isCreate: true,
      }
    },
    createMember(state, { payload }) {
      return {
        ...state,
        isCreate: false,
        memberFile: payload
      }
    },
    err_createMember(state) {
      return {
        ...state,
        isCreate: false,
      }
    },
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
