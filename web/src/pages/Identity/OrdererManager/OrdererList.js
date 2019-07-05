import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Table } from 'antd';
import styles from '../styles.less';

@connect(({ ordererManager }) => {
  return {
    ordererManager
  };
})
class ordererList extends Component {
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ordererManager/handleGetOrdererList'
    });
  }

  handleDeleteorderer = (ordererId) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'ordererManager/handleDeleteOrderer',
      payload: { ordererId }
    });
  }

  handleUpdateorderer = (values) => {
    const { dispatch } = this.props;
    this.props.onChangeTab();
    dispatch({
      type: 'ordererManager/handleGetUpdateList',
      payload: values
    });
  }

  render() {
    const { ordererManager } = this.props;
    const { ordererList, loading } = ordererManager;

    ordererList && ordererList.map((item, i) => {
      return item.key = i
    })

    const ordererListCol = [{
      title: '节点名称',
      dataIndex: 'id'
    }, {
      title: '容器名称',
      dataIndex: 'containerId',
      render: (item) => (<span>{!item ? "空" : item}</span>)
    }, {
      title: '服务端口',
      dataIndex: 'port'
    }, {
      title: '系统通道名称',
      dataIndex: 'systemChainId'
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
          <a onClick={() => this.handleUpdateorderer(item)}>更新</a>
          <a onClick={() => this.handleDeleteorderer(item.id)}>删除</a>
        </div>
      )
    }];

    return (
      <Table loading={loading} dataSource={ordererList} columns={ordererListCol}></Table>
    );
  }
}

const WrapordererList = ordererList;
export default WrapordererList;
