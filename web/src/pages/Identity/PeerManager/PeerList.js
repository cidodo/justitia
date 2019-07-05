import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Table } from 'antd';
import styles from '../styles.less';

@connect(({ peerManager }) => {
  return {
    peerManager
  };
})
class PeerList extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'peerManager/handleGetPeerList'
    });
  }

  handleDeletePeer = (peerId) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'peerManager/handleDeletePeer',
      payload: { peerId }
    });
  }

  handleUpdatePeer = (values) => {
    const { dispatch } = this.props;
    this.props.onChangeTab();
    dispatch({
      type: 'peerManager/handleGetUpdateList',
      payload: values
    });
  }

  render() {
    const { peerManager } = this.props;
    const { peerList, loading } = peerManager;

    peerList && peerList.map((item, i) => {
      return item.key = i
    })

    const peerListCol = [{
      title: '节点名称',
      dataIndex: 'id'
    }, {
      title: '容器名称',
      dataIndex: 'containerId',
      render: (item) => (<span>{!item ? "空" : item}</span>)
    }, {
      title: '节点主机',
      dataIndex: 'ip'
    }, {
      title: '请求地址',
      dataIndex: 'url'
    }, {
      title: '是否使用TLS',
      dataIndex: 'tlsEnable',
      render: (item) => (<span>{item === true ? "是" : "否"}</span>)
    }, {
      title: '是否使用TLS双端校验',
      dataIndex: 'doubleVerity',
      render: (item) => (<span>{item === true ? "是" : "否"}</span>)
    }, {
      title: '操作',
      dataIndex: 'active',
      width: '130px',
      render: (text, item) => (
        <div className={styles.active}>
          <a onClick={() => this.handleUpdatePeer(item)}>更新</a>
          <a onClick={() => this.handleDeletePeer(item.id)}>删除</a>
        </div>
      )
    }];

    return (
      <Table loading={loading} dataSource={peerList} columns={peerListCol}></Table>
    );
  }
}

const WrapPeerList = PeerList;
export default WrapPeerList;
