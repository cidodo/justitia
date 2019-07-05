import { message } from 'antd';
import { getChannelList, getChannelConfig, createChannel, joinChannel, addChannelOrg, getChannelMspId, delChannelOrg, getPeer, addDbcMember } from '@/services/api';


export default {
  namespace: 'ChannelManager',

  state: {
    token: localStorage.getItem('token'),
    loading: false,
    isFetching: false,
    isClose: false,
    isAdd: false,
    isJoin: false,
    createChannel: {
      isFetching: false,
    },
    getChannelConfig: {},
    getChannelList: [],
    getChannelMspId: [],
    peerList: [],
    memberFile: null,
  },

  effects: {
    // 获取channel通道信息
    *handleGetChannelList(_, { call, put }) {
      yield put({ type: 'fetch_getChannelList' });
      const response = yield call(getChannelList);
      try {
        const { code, msg, data } = response;
        if (code === 0) {
          yield put({
            type: 'getChannelList',
            payload: data
          });
        } else {
          yield put({ type: 'err_getChannelList' });
        }
      } catch (error) {
        yield put({ type: 'err_getChannelList' });
      }
    },
    // 获取channel通道配置文件
    *handleGetChannelConfig(_, { call, put }) {
      const response = yield call(getChannelConfig);
      // const { code, msg } = response;
      // if (code === 0) {
      //   yield put({
      //     type: 'getChannelConfig',
      //     payload: response.data
      //   })
      // }
    },
    // 创建通道
    *handleAddChannel({ payload }, { call, put }) {
      yield put({ type: 'fetch_createChannel' });
      const response = yield call(createChannel, payload);
      try {
        const { code, msg } = response;
        if (code === 0) {
          message.success("通道创建成功");
          yield put({ type: 'createChannel' });
          yield put({ type: 'handleGetChannelList' });
        } else {
          message.error(msg);
          yield put({ type: 'err_createChannel' });
        }
      } catch (error) {
        yield put({ type: 'err_createChannel' });
      }
    },
    // 加入通道
    *handleJoinChannel({ payload }, { call, put }) {
      yield put({ type: 'fetch_joinChannel' });
      const response = yield call(joinChannel, payload);
      try {
        const { code, msg } = response;
        if (code === 0) {
          message.success("成功加入通道");
          yield put({ type: 'joinChannel' });
          yield put({ type: 'handleGetChannelList' });
        } else {
          message.error(msg);
          yield put({ type: 'err_joinChannel' });
        }
      } catch (error) {
        yield put({ type: 'err_joinChannel' });
      }
    },
    // 申请添加组织
    *handleAddOrg({ payload }, { call, put }) {
      yield put({ type: 'fetch_addChannelOrg' });
      const response = yield call(addChannelOrg, payload);
      try {
        const { code, msg } = response;
        if (code === 0) {
          message.success("添加组织成功");
          yield put({ type: 'addChannelOrg' });
          yield put({ type: 'handleGetChannelList' });
        } else {
          message.error(msg);
          yield put({ type: 'err_addChannelOrg' });
        }
      } catch (error) {
        yield put({ type: 'err_addChannelOrg' });
      }
    },
    // 申请删除组织
    *handleDelOrg({ payload }, { call, put }) {
      yield put({ type: 'fetch_delChannelOrg' });
      const response = yield call(delChannelOrg, payload);
      try {
        const { code, msg } = response;
        if (code === 0) {
          message.success('删除组织成功');
          yield put({ type: 'delChannelOrg' });
          yield put({ type: 'handleGetChannelList' });
        } else {
          message.error(msg);
          yield put({ type: 'err_delChannelOrg' });
        }
      } catch (error) {
        yield put({ type: 'err_delChannelOrg' });
      }
    },
    // 不动产定制化接口
    *handleAddMember({ payload }, { call, put }) {
      yield put({ type: 'fetch_addChannelOrg' });
      const response = yield call(addDbcMember, payload);
      if (response.code && response.code !== 0) {
        message.error(response.msg);
        yield put({ type: 'err_addChannelOrg' });
      } else {
        message.success("添加组织成功");
        yield put({
          type: 'addMember',
          payload: response
        });
        yield put({ type: 'handleGetChannelList' });
      }
    },
    // 获取指定通道的全部成员
    *handleGetChannelMspId({ payload }, { call, put }) {
      const response = yield call(getChannelMspId, payload);
      const { code, msg } = response;
      try {
        const { code, msg } = response;
        if (code === 0) {
          yield put({
            type: 'getChannelMspId',
            payload: response.data
          });
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 获取peer信息
    *handleGetPeerList({ }, { call, put }) {
      // yield put({ type: 'fetch_peerList' })
      const response = yield call(getPeer);
      try {
        const { data } = response;
        yield put({
          type: 'peerList',
          payload: {
            peerList: data ? data : []
          },
        });
      } catch (error) {
        // yield put({ type: 'failed_peerList' });
      }
    },
  },

  reducers: {
    getChannelList(state, { payload }) {
      return {
        ...state,
        loading: false,
        getChannelList: payload
      }
    },
    fetch_getChannelList(state) {
      return {
        ...state,
        loading: true
      }
    },
    err_getChannelList(state) {
      return {
        ...state,
        loading: false
      }
    },
    getChannelConfig(state, { payload }) {
      return {
        ...state,
        getChannelConfig: payload
      }
    },
    createChannel(state, { payload }) {
      return {
        ...state,
        createChannel: {
          isFetching: false,
        }
      }
    },
    fetch_createChannel(state) {
      return {
        ...state,
        createChannel: {
          isFetching: true,
        }
      }
    },
    err_createChannel(state, { payload }) {
      return {
        ...state,
        createChannel: {
          isFetching: false,
        }
      }
    },
    joinChannel(state, { payload }) {
      return {
        ...state,
        isJoin: false,
      }
    },
    fetch_joinChannel(state) {
      return {
        ...state,
        isJoin: true,
      }
    },
    err_joinChannel(state, { payload }) {
      return {
        ...state,
        isJoin: false
      }
    },
    delChannelOrg(state) {
      return {
        ...state,
        loading: false,
        isClose: true,
      }
    },
    fetch_delChannelOrg(state) {
      return {
        ...state,
        loading: true,
        isClose: false,
      }
    },
    err_delChannelOrg(state) {
      return {
        ...state,
        loading: false,
        isClose: true,
      }
    },
    addChannelOrg(state) {
      return {
        ...state,
        isAdd: false,
      }
    },
    addMember(state, { payload }) {
      return {
        ...state,
        isAdd: false,
        memberFile: payload
      }
    },
    fetch_addChannelOrg(state) {
      return {
        ...state,
        isAdd: true,
      }
    },
    err_addChannelOrg(state) {
      return {
        ...state,
        isAdd: false,
      }
    },
    getChannelMspId(state, { payload }) {
      return {
        ...state,
        getChannelMspId: payload.orgs,
        isClose: false,
      }
    },
    // fetch_peerList(state) {
    //   return {
    //     ...state,
    //     isLoading: true,
    //   }
    // },
    peerList(state, { payload }) {
      return {
        ...state,
        peerList: payload.peerList
      };
    },
    // failed_peerList(state) {
    //   return {
    //     ...state,
    //     isLoading: false,
    //   }
    // },
  }
};
