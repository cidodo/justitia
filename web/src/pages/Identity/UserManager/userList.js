import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Table } from 'antd';
import styles from '../styles.less';

@connect(({ fabricUserManager }) => {
  return {
    fabricUserManager
  };
})
class UserList extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'fabricUserManager/handleGetUserList'
    });
  }

  handleDeleteUser = (userId) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'fabricUserManager/handleDeleteUser',
      payload: { userId }
    });
  }

  handleUpdateUser = (values) => {
    const { dispatch } = this.props;
    this.props.onChangeTab();
    dispatch({
      type: 'fabricUserManager/handleGetUpdateList',
      payload: values
    });
  }

  render() {
    const { fabricUserManager } = this.props;
    const { userList, loading } = fabricUserManager;

    userList && userList.map((item, i) => {
      return item.key = i
    })

    const userListCol = [{
      title: '用户Id',
      dataIndex: 'id'
    }, {
      title: '是否为管理员',
      dataIndex: 'admin',
      render: (item) => (<span>{item ? "管理员" : "非管理员"}</span>)
    }, {
      title: '标签',
      dataIndex: 'tag'
    }, {
      title: '操作',
      dataIndex: 'active',
      width: '130px',
      render: (text, item) => (
        <div className={styles.active}>
          <a onClick={() => this.handleUpdateUser(item)}>更新</a>
          <a onClick={() => this.handleDeleteUser(item.id)}>删除</a>
        </div>
      )
    }];

    return (
      <Table loading={loading} dataSource={userList} columns={userListCol}></Table>
    );
  }
}

const WrapUserList = UserList;
export default WrapUserList;
